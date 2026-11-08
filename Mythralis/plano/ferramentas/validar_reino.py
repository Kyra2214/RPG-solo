# Validador de reino (VALIDADOR_DE_REINO.md). Uso: python validar_reino.py <pasta dados> [saida.txt]  (reino 2 por padrão; reino 3: reinos/03-veu-dos-malditos/dados; reino 5: reinos/05-azeroth/dados; reino 4: reinos/04-vale-palancar/dados)
import sys,os,re,csv,json,glob
from collections import defaultdict,Counter
P=sys.argv[1] if len(sys.argv)>1 else os.path.join(os.path.dirname(os.path.abspath(__file__)),'..','..','reinos','02-coroas-de-pedra-reino-das-montanhas','dados')
BASE=os.path.join(P,'..','..','..')
FIND=[]
def F(cod,sev,ent,msg,fonte='—'): FIND.append((cod,sev,ent,msg,fonte))
def split(l):
    out=[];cur='';i=0
    while i<len(l):
        if l[i]=='\\' and i+1<len(l): cur+=l[i:i+2];i+=2;continue
        if l[i]=='|': out.append(cur);cur=''
        else: cur+=l[i]
        i+=1
    out.append(cur); return [c.replace('\\|','|').replace('\\\\','\\') for c in out]
def load(name):
    p=os.path.join(P,name); L=open(p,encoding='utf-8').read().split('\n')
    if not L or L[0].strip()!='# esquema=1': F('V-01','E',name,'falta "# esquema=1" na 1ª linha')
    L=[l for l in L if l and not l.startswith('#')]
    h=split(L[0]); rows=[]
    for n,l in enumerate(L[1:],2):
        c=split(l)
        if len(c)!=len(h): F('V-01','E',name,'linha %d com %d campos (esperado %d)'%(n,len(c),len(h))); continue
        rows.append(dict(zip(h,c)))
    return h,rows
T={}
SPEC={ # arquivo: (prefixo, id, campos obrigatórios)
'reino.txt':('kingdom.','id',None),'cidade.txt':('city.','id',None),'assentamento.txt':('settlement.','id',None),'npc.txt':('npc.','id',None),
'faccao.txt':('faction.','id',None),'rota.txt':('route.','id',None),'relogio.txt':('clock.','id',None),'evento.txt':('event.','id',None),
'semente.txt':('seed.','id',None),'misterio.txt':('mist.','id',None),'fato.txt':('fact.','id',None),'reputacao.txt':('rep.','id',None),
'canal.txt':('canal.','id',None),'cena.txt':('cena.','id',None),'calendario.txt':('calendario.','id',None),'resumo.txt':('','id',None)}
HDR={}
for f in list(SPEC)+['recurso.txt','relacao.txt','povo_no_reino.txt','excecoes.txt','ids_aposentados.txt']:
    h,r=load(f); T[f]=r; HDR[f]=h
ALL={}  # id -> (tipo arquivo)
APOS={r.get('id') for r in T['ids_aposentados.txt']}
# V-01 vazios
for f,rows in T.items():
    for r in rows:
        for k,v in r.items():
            if v.strip()=='' : F('V-01','E',r.get('id',r.get('recurso',f)),'campo vazio: %s (%s)'%(k,f))
# V-02,03,04
mundo=set()
_own=re.search(r'reinos[\\/](\d+)-',os.path.abspath(P)); _n=int(_own.group(1)) if _own else 2
for p in [q for d in sorted(glob.glob(os.path.join(BASE,'reinos','0*-*'))) if int(os.path.basename(d)[:2])<_n for q in glob.glob(os.path.join(d,'*.csv'))]:
    for r in csv.reader(open(p,encoding='utf-8',errors='replace')):
        if r and re.match(r'^[a-z]+\.[a-z0-9.-]+$',r[0]): mundo.add(r[0])
for f,(pre,_,_) in SPEC.items():
    if f=='resumo.txt': continue
    for r in T[f]:
        i=r['id']
        if not re.match(r'^[a-z]+\.[a-z0-9]+(?:[.-][a-z0-9]+)*$',i) or not i.startswith(pre): F('V-02','E',i,'ID fora do padrão prefixo.slug (%s)'%pre,f)
        if i in ALL: F('V-03','E',i,'ID duplicado no reino',f)
        ALL[i]=f
        if i in mundo: F('V-03','E',i,'ID já existe em Aeltharyn',f)
        if i in APOS: F('V-04','E',i,'ID aposentado',f)
# nomes
byname=defaultdict(list)
for f in ['cidade.txt','assentamento.txt','npc.txt','faccao.txt']:
    for r in T[f]: byname[r['nome']].append((f,r['id']))
for n,l in byname.items():
    if len(l)>1:
        F('V-05','E' if len({x[0] for x in l})==1 else 'A',l[0][1],'nome repetido "%s": %s'%(n,', '.join(x[1] for x in l)))
# V-06
for f,(pre,_,_) in SPEC.items():
    rs=T[f]; prop=0
    for r in rs:
        fo=r.get('fonte','')
        if f!='resumo.txt' and not fo.strip(): F('V-06','E',r['id'],'sem fonte',f)
        if 'proposto' in fo.lower(): prop+=1
    if prop: F('V-06','A',f,'%d de %d registros com fonte "proposto" (aguardam o autor)'%(prop,len(rs)),f)
KR=T['reino.txt'][0]; estado=KR['estado']; K=KR['id']
cat=json.load(open(os.path.join(BASE,'referencia','dados-brutos','personagem-catalogo.json'),encoding='utf-8'))
RACE={x['id'] for x in cat['races']}; SUB={x['id']:x['parentRaceId'] for x in cat['subraces']}; CLS={x['id'] for x in cat['classes']}; PRO={x['id'] for x in cat['professions']}
ids=set(ALL)
rn={r['recurso'] for r in T['recurso.txt']}
def toks(s): return [x.strip() for x in re.split(r'[;]',s) if x.strip() and x.strip() not in('?','—','--')]
def ref(ent,field,val,prefixes,f,sev='E'):
    for t in toks(val):
        if re.match(r'^[a-z]+\.',t):
            if t not in ids or not any(t.startswith(p) for p in prefixes): F('V-10',sev,ent,'%s aponta para ID inexistente ou de tipo errado: %s'%(field,t),f)
npc={r['id']:r for r in T['npc.txt']}; city={r['id']:r for r in T['cidade.txt']}; ast={r['id']:r for r in T['assentamento.txt']}; fac={r['id']:r for r in T['faccao.txt']}
LOC=set(city)|set(ast)
FAIXAS=('pequena','média','grande','parte da cidade-mãe','mínima e sazonal','indefinida')  # faixas de uso do reino 2 (doc 05 §4); nenhuma é número
for r in T['cidade.txt']:
    i=r['id']; 
    if r['reino']!=K: F('V-11','E',i,'reino da cidade inexistente','cidade.txt')
    ref(i,'governante',r['governante'],['npc.'],'cidade.txt'); ref(i,'faccoes',r['faccoes'],['faction.'],'cidade.txt')
    for p in toks(r['povos']):
        if p not in RACE: F('V-12','E',i,'povo fora do catálogo: '+p,'cidade.txt')
    if len(toks(r['povos']))<2: F('V-36','A',i,'cidade com menos de 2 povos','cidade.txt')
    if r['governante'] in npc and npc[r['governante']]['vivo']!='sim': F('V-14','E',i,'governante não vivo','cidade.txt')
    if r['governante']=='?': F('V-14','A',i,'governante sem NPC (governo coletivo ou lacuna)','cidade.txt')
    for k in ['localizacao','funcao']:
        if r[k] in('?',''): F('V-16','E',i,'sem '+k,'cidade.txt')
    if r['acesso'] not in('navegavel','terrestre','ambos','?'): F('V-24','E',i,'acesso inválido','cidade.txt')
    if r['populacao_faixa'] not in FAIXAS: F('V-52','E',i,'faixa de população inválida: '+r['populacao_faixa'],'cidade.txt')
for r in T['assentamento.txt']:
    i=r['id']
    if r['cidade_mae'] not in city: F('V-11','E',i,'cidade-mãe inexistente','assentamento.txt')
    elif city[r['cidade_mae']]['reino']!=r['reino']: F('V-11','E',i,'cidade-mãe de outro reino','assentamento.txt')
    ref(i,'lideranca',r['lideranca'],['npc.'],'assentamento.txt')
    if r['lideranca'] not in npc or npc[r['lideranca']]['vivo']!='sim': F('V-14','E',i,'liderança não é NPC vivo','assentamento.txt')
    if r['acesso']=='?': F('V-27','A',i,'acesso com duração desconhecida','assentamento.txt')
    if r['populacao_faixa'] not in FAIXAS: F('V-52','E',i,'faixa de população inválida','assentamento.txt')
principais=[r for r in T['npc.txt'] if r['principal']=='sim']
for r in T['npc.txt']:
    i=r['id']
    if r['povo'] not in RACE: F('V-12','E',i,'povo fora do catálogo: '+r['povo'],'npc.txt')
    if r['subraca'] not in('—','?'):
        if r['subraca'] not in SUB: F('V-12','E',i,'sub-raça fora do catálogo','npc.txt')
        elif SUB[r['subraca']]!=r['povo']: F('V-12','E',i,'sub-raça não pertence ao povo','npc.txt')
    if r['classe'] not in CLS and not (r['classe']=='—' and r['vivo']=='não'): F('V-12','E',i,'classe fora do catálogo: '+r['classe'],'npc.txt')
    if r['profissao'] not in PRO and r['profissao']!='—': F('V-12','E',i,'profissão fora do catálogo: '+r['profissao'],'npc.txt')
    if r['cidade']!='—' and r['cidade'] not in city: F('V-10','E',i,'cidade inexistente','npc.txt')
    if r['faccao']!='—' and r['faccao'] not in fac: F('V-10','E',i,'facção inexistente','npc.txt')
    if r['cidade']=='—' and r['faccao']=='—': F('V-15','A',i,'NPC sem cidade nem facção','npc.txt')
    if r['vivo'] not in('sim','não','desaparecido','exilado'): F('V-01','E',i,'vivo inválido','npc.txt')
    if r['principal']=='sim':
        for k in['objetivo','agenda','pressao','capacidades','limites']:
            if r[k]=='?': F('V-35','A' if estado!='fechado' else 'E',i,'principal com %s = ?'%k,'npc.txt')
        if r['tom']=='?': F('V-74','A',i,'principal sem tom','npc.txt')
ag=Counter(r['agenda'] for r in principais); pr=Counter(r['pressao'] for r in principais)
for r in principais:
    if ag[r['agenda']]>1 or pr[r['pressao']]>1: F('V-35','A',r['id'],'agenda ou pressão idêntica a outro principal','npc.txt')
MIN='A' if estado!='fechado' else 'E'
if len(T['cidade.txt'])<10: F('V-30',MIN,K,'menos de 10 cidades')
if len(principais)<8: F('V-32',MIN,K,'menos de 8 principais (%d)'%len(principais))
vivos=[r for r in T['npc.txt'] if r['vivo']=='sim']
for pap in('heroi','general'):
    n=len([r for r in vivos if pap in toks(r['papeis'])])
    if n<2: F('V-33',MIN,K,'menos de 2 NPCs vivos com papel '+pap)
if not [r for r in vivos if 'governante' in toks(r['papeis'])]: F('V-34',MIN,K,'sem NPC governante')
if not [r for r in T['npc.txt'] if 'herdeiro' in toks(r['papeis'])]: F('V-34','A',K,'sem herdeiro; sucessão do presidente do Concílio é lacuna explícita (doc 04 §8)' if 'coroas' in K else ('sem herdeiro; reino sem rei: o Capitão de Marcha é escolhido por prova ou aclamação e o desfecho da sucessão é desconhecido (doc 04 §5)' if 'palancar' in K else ('sem herdeiro; sucessão por coroa, desfecho desconhecido (doc 04 §2.2)' if 'azeroth' in K else 'sem herdeiro; sucessão só pela Provação das Armas, desfecho desconhecido (doc 04 §5)')))
F('V-31','A','mundo','mundo com 1 reino em composição (reinos 3 a 5 e Aeltharyn fora deste conjunto); mínimo 5 só no nível mundo')
# facções
for r in T['faccao.txt']:
    i=r['id']; ref(i,'lideranca',r['lideranca'],['npc.'],'faccao.txt'); ref(i,'aliados',r['aliados'],['faction.'],'faccao.txt'); ref(i,'rivais',r['rivais'],['faction.'],'faccao.txt'); ref(i,'presenca_cidades',r['presenca_cidades'],['city.'],'faccao.txt'); ref(i,'presenca_assentamentos',r['presenca_assentamentos'],['settlement.'],'faccao.txt')
    if r['lideranca'] not in npc: F('V-41','E',i,'liderança inexistente','faccao.txt')
    elif npc[r['lideranca']]['vivo']!='sim': F('V-14','E',i,'liderança não viva','faccao.txt')
    both=set(toks(r['aliados']))&set(toks(r['rivais']))
    if both: F('V-40','E',i,'aliado e rival: '+', '.join(both),'faccao.txt')
    if r['aliados']=='?' or r['rivais']=='?': F('V-42','A',i,'aliados ou rivais desconhecidos','faccao.txt')
tp=defaultdict(set)
for r in T['relacao.txt']:
    o,d,dt,t,fo=r['origem'],r['destino'],r['destino_texto'],r['tipo'],r['fonte']
    if not o or (d in('--','?') and dt in('--','?')) or not t or not fo: F('V-43','E',o,'relação sem origem, destino ou fonte','relacao.txt')
    if d.startswith(('npc.','faction.','city.','settlement.')) and d not in ids: F('V-10','E',o,'relação aponta para ID inexistente: '+d,'relacao.txt')
    if o not in ids: F('V-10','E',o,'relação com origem inexistente','relacao.txt')
    tp[frozenset([o,d])].add(t)
for k,v in tp.items():
    if {'aliança','rivalidade'}<=v: F('V-40','A',' × '.join(sorted(k)),'tipos opostos na mesma dupla','relacao.txt')
# rotas
adj=defaultdict(set); adj_all=defaultdict(set); seen=set()
for r in T['rota.txt']:
    i=r['id']; a,b=r['de'],r['para']
    if a not in LOC or b not in LOC or a==b: F('V-20','E',i,'rota não liga dois locais existentes e distintos','rota.txt'); continue
    k=(frozenset([a,b]),r['meio'])
    if k in seen: F('V-21','E',i,'rota duplicada (mesmo par e meio)','rota.txt')
    seen.add(k)
    lo,hi=r['duracao_min'],r['duracao_max']
    if (lo=='?')!=(r['estado']=='desconhecida') or (hi=='?')!=(r['estado']=='desconhecida'): F('V-22','E',i,'estado e duração incoerentes','rota.txt')
    if lo!='?' and hi!='?' and float(lo)>float(hi): F('V-22','E',i,'duração mínima maior que a máxima','rota.txt')
    adj_all[a].add(b);adj_all[b].add(a)
    if r['estado']!='desconhecida': adj[a].add(b);adj[b].add(a)
def conexo(g):
    cs=set(city); 
    if not cs: return True
    s=[next(iter(cs))];vis={s[0]}
    while s:
        x=s.pop()
        for y in g[x]:
            if y not in vis: vis.add(y);s.append(y)
    return cs<=vis
if not conexo(adj_all): F('V-23','E',K,'cidades não formam grafo conexo nem contando rotas desconhecidas')
elif not conexo(adj): F('V-23','A',K,'cidades conexas só contando rotas de duração desconhecida (nenhuma rota documentada com tempo)','rota.txt')
for i,r in city.items():
    if r['acesso'] in('navegavel','ambos') and not [x for x in T['rota.txt'] if i in(x['de'],x['para']) and x['meio'] in('rio','mar')]: F('V-24','E',i,'acesso navegável sem rota rio ou mar','cidade.txt')
    if r['acesso']=='terrestre' and [x for x in T['rota.txt'] if i in(x['de'],x['para']) and x['meio']=='mar']: F('V-24','E',i,'cidade terrestre com rota mar','cidade.txt')
    if re.search(r'minério|metal',r['recurso_chave']) and not [x for x in T['recurso.txt'] if x['localidade']==i and x['papel']=='produtor' and x['recurso'] in('metal','minério')]: F('V-25','E',i,'recurso-chave de minério sem produtor em recurso.txt','cidade.txt')
    if not any(i in(x['de'],x['para']) for x in T['rota.txt']) and r['acesso']!='?': F('V-16','E',i,'cidade sem rota nem acesso "?"','cidade.txt')
    if not any(i in(x['de'],x['para']) for x in T['rota.txt']): F('V-44','E',i,'cidade isolada: sem rota','rota.txt')
    rr=[x for x in T['recurso.txt'] if x['localidade']==i]
    if not rr: F('V-44','E',i,'cidade sem dependência em recurso.txt','recurso.txt')
    elif all(x['papel']=='?' for x in rr): F('V-44','A',i,'dependências só com papel ?','recurso.txt')
F('V-26','A',K,'rio, posição de fortaleza e relevo: revisão manual contra M-04 (não verificável por regra)','M-04')
# recursos
for r in T['recurso.txt']:
    e=r['recurso']+'@'+r['localidade']
    if r['localidade'] not in LOC and r['localidade']!=K and not r['localidade'].startswith('faction.'): F('V-50','E',e,'localidade inexistente','recurso.txt')
    if r['demanda']!='?' and not re.match(r'^[0-5]$',r['demanda']): F('V-51','E',e,'demanda fora de 0 a 5','recurso.txt')
    for k in('producao','reserva'):
        v=r[k]
        if v!='?' and re.match(r'^[0-5]',v) and '(provisório' not in v: F('V-51','E',e,'estimativa sem "(provisório": '+k,'recurso.txt')
    if r['papel'] not in('produtor','consumidor','transportador','?'): F('V-01','E',e,'papel inválido','recurso.txt')
# relógios
for r in T['relogio.txt']:
    i=r['id']
    if int(r['progresso'])>int(r['segmentos']): F('V-60','E',i,'progresso maior que segmentos','relogio.txt')
    if r['move']=='?' or r['trava']=='?': F('V-60','E',i,'move ou trava = ?','relogio.txt')
    ref(i,'entidades',r['entidades'],['city.','npc.','faction.','settlement.'],'relogio.txt')
# eventos
rotaids={r['id'] for r in T['rota.txt']}
for r in T['evento.txt']:
    i=r['id']
    if r['gatilho']=='?' or r['cadeia']=='?': F('V-61','E',i,'sem gatilho ou cadeia','evento.txt')
    for k,pre in(('relogio',['clock.']),('cidades_e_assentamentos',['city.','settlement.']),('pessoas_e_faccoes',['npc.','faction.']),('rotas',['route.'])): ref(i,k,r[k],pre,'evento.txt')
    for t in toks(r['recursos']):
        if t not in rn: F('V-61','E',i,'recurso inexistente: '+t,'evento.txt')
# sementes
cnt=defaultdict(set)
for r in T['semente.txt']:
    i=r['id']; ref(i,'entidade',r['entidade'],['city.','settlement.'],'semente.txt'); ref(i,'agentes',r['agentes'],['npc.','faction.','settlement.'],'semente.txt'); ref(i,'relogio_evento',r['relogio_evento'],['clock.','event.'],'semente.txt')
    if r['porte'] not in('P','M','G'): F('V-62','E',i,'porte inválido','semente.txt')
    if not re.match(r'^[1-4]$',r['ameaca']): F('V-62','E',i,'ameaça fora de 1 a 4','semente.txt')
    cnt[r['entidade']].add(r['porte'])
    for a in toks(r['agentes']):
        if a in npc and npc[a]['vivo']!='sim': F('V-13','E',i,'agente morto: '+a,'semente.txt')
for e in LOC:
    if cnt[e]!={'P','M','G'}: F('V-62','A',e,'falta semente de algum porte','semente.txt')
for r in T['evento.txt']:
    for a in toks(r['pessoas_e_faccoes']):
        if a in npc and npc[a]['vivo']!='sim': F('V-13','E',r['id'],'agente morto: '+a,'evento.txt')
for r in T['misterio.txt']:
    i=r['id']
    if len(toks(r['hipoteses_abertas']))<2: F('V-63','E',i,'menos de 2 hipóteses abertas','misterio.txt')
    if r['a_verdade_nao_pode_ser'] in('?',''): F('V-63','E',i,'sem "a verdade não pode ser"','misterio.txt')
    ref(i,'entidades',r['entidades'],['npc.','faction.','city.','settlement.'],'misterio.txt'); ref(i,'relogio_evento',r['relogio_evento'],['clock.','event.'],'misterio.txt')
c=T['calendario.txt'][0]
if int(c['meses'])*int(c['dias_por_mes'])+int(c['dias_de_transicao'])!=365: F('V-64','E',c['id'],'dias não somam 365','calendario.txt')
if c['estacao_inicial'] not in toks(c['estacoes']): F('V-64','E',c['id'],'estação inicial inexistente','calendario.txt')
# fatos
FA={r['id']:r for r in T['fato.txt']}
for r in T['fato.txt']:
    i=r['id']
    if r['camada'] not in('publico','local','restrito','secreto') or r['estado'] not in('confirmado','alegado','suspeita','rumor','inferência','desconhecido') or r['caminho']=='?': F('V-70','E',i,'camada, estado ou caminho inválido','fato.txt')
    ref(i,'entidade',r['entidade'],['npc.','faction.','city.','settlement.','clock.','mist.'],'fato.txt'); ref(i,'quem_sabe',r['quem_sabe'],['npc.','faction.'],'fato.txt')
    if r['camada'] in('restrito','secreto'):
        if (r['quem_sabe'] in('—','?')) and not r['entidade'].startswith('clock.'): F('V-70','E',i,'restrito ou secreto sem quem_sabe','fato.txt')
        if r['marcador'] in('—','?',''): F('V-70','E',i,'restrito ou secreto sem marcador','fato.txt')
# resumos
RS={r['id']:r for r in T['resumo.txt']}
for i,r in RS.items():
    if len(r['resumo'])>500 or len(r['vizinhanca'])>300: F('V-71','E',i,'resumo > 500 ou vizinhança > 300','resumo.txt')
    if i not in ids: F('V-10','E',i,'resumo de entidade inexistente','resumo.txt')
for i in ids:
    if i.split('.')[0] in('city','settlement','npc','faction','clock') and i not in RS: F('V-74','A',i,'entidade sem resumo','resumo.txt')
cnt_r=Counter(r['id'] for r in T['resumo.txt'])
for i,n in cnt_r.items():
    if n>1: F('V-71','E',i,'mais de um resumo','resumo.txt')
for f in FA.values():
    if f['camada'] in('restrito','secreto'):
        for i,r in RS.items():
            if r['camada_max'] in('publico','local') and f['marcador'].lower() in (r['resumo']+' '+r['vizinhanca']).lower(): F('V-72','E',i,'contém marcador de '+f['id'],'resumo.txt')
# cenas
for r in T['cena.txt']:
    i=r['id']
    if int(r['caracteres'])>2400: F('V-73','E',i,'entra > 2.400 caracteres','cena.txt')
    for t in toks(r['entra_ids']):
        if t.startswith('clock.'): F('V-73','E',i,'relógio na cena','cena.txt')
        elif t.startswith('fact.'):
            if t not in FA or FA[t]['camada'] not in('publico','local'): F('V-73','E',i,'fato não público nem local: '+t,'cena.txt')
        elif t.startswith('npc.') and t in npc and npc[t]['vivo']!='sim': F('V-73','E',i,'NPC morto: '+t,'cena.txt')
        elif t.startswith(('route.',)) : pass
        elif t not in ids: F('V-10','E',i,'cena aponta para ID inexistente: '+t,'cena.txt')
for r in T['reputacao.txt']:
    ref(r['id'],'entidade',r['entidade'],['city.','faction.'],'reputacao.txt'); ref(r['id'],'quem_reage',r['quem_reage'],['npc.','faction.'],'reputacao.txt')
for r in T['canal.txt']: ref(r['id'],'entidades',r['entidades'],['faction.','city.','npc.','settlement.'],'canal.txt')
for r in T['povo_no_reino.txt']:
    if r['povo'] not in RACE: F('V-12','E',r['povo'],'povo fora do catálogo','povo_no_reino.txt')
    ref(r['povo'],'cidades',r['cidades'],['city.'],'povo_no_reino.txt')
# exceções
EX=T['excecoes.txt']; exk=set()
for r in EX:
    if not r['fonte'].strip() or r['fonte']=='?' or r['regra']=='': F('V-99','E',r['entidade'],'exceção sem fonte','excecoes.txt')
    if r['entidade'] not in ids and r['entidade']!='—': F('V-99','E',r['entidade'],'exceção aponta para entidade inexistente','excecoes.txt')
    exk.add((r['regra'],r['entidade']))
out=[];used=set()
for cod,sev,ent,msg,fo in FIND:
    if sev=='E' and (cod,ent) in exk: sev='A'; msg='[exceção] '+msg; used.add((cod,ent))
    out.append((cod,sev,ent,msg,fo))
out.sort(key=lambda x:(x[1]!='E',x[0],x[2]))
lines=['|'.join(x) for x in out]
E_=sum(1 for x in out if x[1]=='E'); A_=sum(1 for x in out if x[1]=='A')
lines.append('# resumo: %d erros, %d avisos, %d exceções aplicadas'%(E_,A_,len(used)))
dest=sys.argv[2] if len(sys.argv)>2 else os.path.join(P,'relatorio-validador.txt')
open(dest,'w',encoding='utf-8',newline='\n').write('codigo|severidade|entidade|mensagem|fonte\n'+'\n'.join(lines)+'\n')
print(lines[-1]); 
c=Counter((x[0],x[1]) for x in out); print(sorted(c.items()))
