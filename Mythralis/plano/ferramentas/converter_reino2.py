# Conversor R2-7.1: CSVs e docs do reino 2 -> dados/*.txt (ESQUEMA_DE_DADOS.md, D-21 A). Só reorganiza; não cria fato.
import csv,re,os,json,sys
B=os.path.join(os.path.dirname(os.path.abspath(__file__)),'..','..')
D=os.path.join(B,'reinos','02-coroas-de-pedra-reino-das-montanhas'); O=os.path.join(D,'dados'); os.makedirs(O,exist_ok=True)
K='kingdom.coroas-de-pedra'
def rd(f): return list(csv.DictReader(open(os.path.join(D,f),encoding='utf-8')))
def esc(v): return str(v).replace('\\','\\\\').replace('|','\\|').replace('\r','').replace('\n',' ').strip() or '?'
def wr(name,cols,rows):
    with open(os.path.join(O,name),'w',encoding='utf-8',newline='\n') as f:
        f.write('# esquema=1\n'+'|'.join(cols)+'\n')
        for r in rows: f.write('|'.join(esc(r.get(c,'?')) for c in cols)+'\n')
def lst(s):
    ids=re.findall(r'(?:npc|city|faction|settlement|clock|event|route)\.[a-z0-9-]+',s)
    return ';'.join(dict.fromkeys(ids)) if ids else ('?' if not s.strip() else ';'.join(x.strip() for x in s.split(';') if x.strip()))
txt=lambda f:open(os.path.join(D,f),encoding='utf-8').read()
REG=rd('12-registro-mestre.csv'); byid={r['id']:r for r in REG}
doc1=txt('01-reino-das-coroas-de-pedra.md')
cat=json.load(open(os.path.join(B,'referencia','dados-brutos','personagem-catalogo.json'),encoding='utf-8'))
# povos: nome -> id (doc 10 §1)
povo={}
for l in txt('10-matriz-povos-nativos-cidades-e-forasteiros.md').split('\n'):
    m=re.match(r'\|\s*\d+\s*\|\s*([^|]+?)\s*\|\s*`([^`]+)`',l)
    if m: povo[m.group(1)]=m.group(2)
fid={r['ficha_doc']:r['id'] for r in REG if r['tipo'].startswith('fac')}
nome2npc={r['nome']:r['id'] for r in REG if r['tipo']=='npc'}
# reino
wr('reino.txt',['id','nome','mundo','tema','estado','capital','calendario','fonte'],[dict(id=K,nome='Coroas de Pedra',mundo='mundo.mythralis',tema='reino das montanhas e cidades subterrâneas',estado='em_composicao',capital='city.pedra-ata',calendario='calendario.contagem-das-atas',fonte='D-26; D-31; capital = sede do Concílio (ASSUMINDO)')])
wr('calendario.txt',['id','meses','dias_por_mes','dias_de_transicao','estacoes','ano_inicial','estacao_inicial','fonte'],[dict(id='calendario.contagem-das-atas',meses=12,dias_por_mes=30,dias_de_transicao=5,estacoes='Gelo Fechado;Degelo;Passo Aberto;Antes-da-Neve',ano_inicial='516 das Atas (= 742 de Aeltharyn)',estacao_inicial='Gelo Fechado',fonte='doc 01 parte 5; mundo/M-02')])
# recursos
RC=rd('12-recursos.csv')
def prole(p):
    w=p.split(' ')[0].strip(';,').lower()
    return {'produtor':'produtor','consumidor':'consumidor','transportador':'transportador'}.get(w,'?')
rec=[]
for r in RC:
    rec.append(dict(recurso=r['recurso'],localidade=r['localidade'],papel=prole(r['papel']),producao=r['producao'],reserva=r['reserva'],demanda=r['demanda'],estado_evidencia=r['estado_evidencia'],nota=(r['papel']+'; '+r['nota']),fonte=r['fonte']))
wr('recurso.txt',['recurso','localidade','papel','producao','reserva','demanda','estado_evidencia','nota','fonte'],rec)
chave={}
for r in RC:
    if prole(r['papel'])=='produtor' and r['localidade'].startswith('city.'): chave.setdefault(r['localidade'],[]).append(r['recurso'])
# cidades
secs=re.split(r'(?=^### 2\.\d+ )',doc1,flags=re.M)
res={r['id']:r for r in rd('14-resumos-de-contexto.csv')}
rows=[]
def fld(s,k):
    m=re.search(r'\*\*'+re.escape(k)+r':\*\*\s*(.*?)(?:\s{2}\n|\n|$)',s); return m.group(1).strip() if m else '?'
for s in secs:
    m=re.search(r'\*\*ID:\*\*\s*`([^`]+)`',s)
    if not m or not s.startswith('### 2.'): continue
    cid=m.group(1); pop=re.search(r'faixa \*\*(\w+)\*\*',fld(s,'População')); pov=re.search(r'\*\*Povos presentes:\*\*\s*(.*?)(?:\s{2}\n|\n)',s)
    pv=[povo[x.strip().rstrip('.')] for x in pov.group(1).split(';')]
    gov=fld(s,'Governo local'); g=[v for k,v in nome2npc.items() if k in gov]
    fac=[fid[x] for x in re.findall(r'F-\d+',fld(s,'Facções presentes'))]
    rows.append(dict(id=cid,reino=K,nome=byid[cid]['nome'],tipo=fld(s,'Tipo').rstrip('.'),populacao_faixa=pop.group(1),localizacao=fld(s,'Localização'),governo=gov,governante=g[0] if g else '?',economia=fld(s,'Economia'),defesas=fld(s,'Defesas e vulnerabilidades'),locais=fld(s,'Locais notáveis'),problemas=fld(s,'Problemas atuais'),faccoes=';'.join(fac) or '?',rumores='[rumor] '+fld(s,'Rumores (não confirmados)'),lenda=fld(s,'Lenda'),funcao=fld(s,'Função narrativa'),povos=';'.join(pv),acesso='?',recurso_chave=';'.join(chave.get(cid,[])) or '?',fonte='doc 01 '+byid[cid]['ficha_doc']+'; proposto (D-31)'))
wr('cidade.txt',list(rows[0].keys()),rows)
# assentamentos
dem={r['id']:r for r in rd('12-demografia.csv')}
rows=[]
for r in rd('12-assentamentos.csv'):
    g=byid[r['id']]; rows.append(dict(id=r['id'],reino=K,cidade_mae=r['cidade_mae'],nome=g['nome'],tipo=dem[r['id']]['categoria'],populacao_faixa=dem[r['id']]['faixa_uso'],lideranca=r['lideranca_id'],obrigacao=r['obrigacao'],interesse_local=r['interesse_local'],tensao=r['tensao'],acesso='?',fonte='doc 03 '+g['ficha_doc']+'; doc 07; proposto (D-32)'))
wr('assentamento.txt',list(rows[0].keys()),rows)
# npcs
comp={r['id']:r for r in rd('12-competencias.csv')}; fich={r['id']:r for r in rd('12-fichas.csv')}; voz={r['npc_id']:r for r in rd('12-voz.csv')}
cat_ficha={}
for m in re.finditer(r'\*\*ID:\*\*\s*`(npc\.[^`]+)`.*?\*\*Categoria:\*\*\s*([^;]+);',doc1): cat_ficha[m.group(1)]=m.group(2).strip()
PAP={'governante':'governante','general':'general','herói':'heroi','guardiã':'guardiao','guardião':'guardiao','benfeitora':'benfeitor','benfeitor':'benfeitor','memória':'outro','guia':'outro','mestra de fogo':'outro','arquivista':'outro'}
rows=[]
for r in REG:
    if r['tipo']!='npc': continue
    i=r['id']; c=comp.get(i,{}); f=fich.get(i); v=voz.get(i); p=r['papel']
    if 'lendário' in p: papeis='heroi'
    elif i in cat_ficha: papeis=PAP.get(cat_ficha[i],'outro')
    else: papeis='outro'
    pr='sim' if f else 'não'
    sub=c.get('subraca_id','--'); sub='—' if sub in('--','') else sub
    rows.append(dict(id=i,reino=K,nome=r['nome'],idade=(f['idade'] if f else '?'),povo=c.get('raca_id') or r['povo_id'],subraca=sub,classe=c.get('classe_id','?'),profissao=c.get('profissao_id','?'),cidade=r['cidade'] or '—',faccao='—',cargo=(f['cargo'] if f else p),papeis=papeis,principal=pr,vivo=r['vivo'],personalidade=(f['personalidade'] if f else '?'),objetivo=(f['agenda'] if f else '?'),limites=c.get('limites','?'),capacidades=c.get('capacidades','?'),agenda=(f['agenda'] if f else '?'),pressao=(f['pressao'] if f else '?'),tom=(v['tom'] if v else '?'),resumo=res[i]['resumo'],fonte='doc 01; 12-competencias.csv; proposto ('+r['situacao']+'); objetivo = agenda (ASSUMINDO)'))
wr('npc.txt',list(rows[0].keys()),rows)
# facções
rows=[]
for r in rd('12-facoes.csv'):
    rows.append(dict(id=r['id'],reino=K,nome=r['nome'],lideranca=r['lideranca_id'],objetivo=r['objetivo'],recursos=r['recursos'],metodos=r['metodos'],tensao=r['tensao'],aliados=lst(r['aliados']) if 'faction.' in r['aliados'] else '?',rivais=lst(r['rivais']) if 'faction.' in r['rivais'] else '?',presenca_cidades=lst(r['presenca_cidades']),presenca_assentamentos=lst(r['presenca_assentamentos']),fonte='doc 04 '+byid[r['id']]['ficha_doc']+'; proposto (D-34)'))
wr('faccao.txt',list(rows[0].keys()),rows)
# rotas: só ligações com dois locais existentes
rows=[]
cities=[r['id'] for r in REG if r['tipo']=='cidade']
def rota(i,a,b,cond,est,fonte): rows.append(dict(id=i,reino=K,de=a,**{'para':b},meio='?',duracao_min='?',duracao_max='?',unidade='dia',estado='desconhecida',condicao=cond,fonte=fonte))
for c in cities:
    if c!='city.pedra-ata': rota('route.concilio-'+c.split('.')[1]+'-pedra-ata',c,'city.pedra-ata','delegações ao Pátio das Delegações; duração desconhecida','d','doc 07 route.concilio-pedra-ata')
for i,a,b in [('route.altavento-pedra-ata','city.altavento','city.pedra-ata'),('route.barcaalta-portao-cinza','city.barcaalta','city.portao-cinza'),('route.barcaalta-rodaviva','city.barcaalta','city.rodaviva'),('route.barcaalta-brasalume','city.barcaalta','city.brasalume')]:
    rota(i,a,b,'sugerida por dependência; sem percurso descrito; duração desconhecida','d','doc 07 '+i)
cols=list(rows[0].keys()); cols.insert(cols.index('para'),'para') if False else None
wr('rota.txt',['id','reino','de','para','meio','duracao_min','duracao_max','unidade','estado','condicao','fonte'],rows)
# relógios
rows=[]
for r in rd('12-relogios.csv'):
    a,b=r['progresso_inicial'].split('/')
    rows.append(dict(id=r['clock_id'],reino=K,nome=r['nome'],segmentos=b,progresso=a,move=r['o_que_move'],trava=r['o_que_trava'],paga=r['quem_paga'],completa=r['ao_completar'],inacao=r['efeito_da_inacao'],entidades=lst(r['entidades']),fonte=r['fonte']))
wr('relogio.txt',list(rows[0].keys()),rows)
# relações
rows=[dict(origem=r['origem_id'],destino=r['destino_id'],destino_texto=r['destino_texto'],tipo=r['tipo'],descricao=r['descricao'],fonte=r['fonte'],certeza=r['certeza']) for r in rd('12-relacoes.csv')]
wr('relacao.txt',['origem','destino','destino_texto','tipo','descricao','fonte','certeza'],rows)
# demais (renomeia colunas de chave)
def conv(src,name,ren,cols,extra=None):
    rows=[]
    for r in rd(src):
        x={ren.get(k,k):v for k,v in r.items()}; x['reino']=K
        if extra: x.update(extra(r))
        rows.append(x)
    wr(name,cols,rows)
conv('14-camadas-de-informacao.csv','fato.txt',{'fact_id':'id'},['id','reino','entidade','fato','camada','estado','quem_sabe','evidencia','caminho','como_descobrir','marcador','fonte'],lambda r:{'marcador':r['marcador'] or '—'})
conv('12-eventos.csv','evento.txt',{'evento_id':'id'},['id','reino','nome','gatilho','relogio','cadeia','cidades_e_assentamentos','recursos','pessoas_e_faccoes','rotas','fonte'])
conv('13-sementes-de-aventura.csv','semente.txt',{'seed_id':'id'},['id','reino','entidade','porte','ameaca','titulo','gancho','agentes','abordagens_possiveis','se_ninguem_age','relogio_evento','fonte'],lambda r:{'relogio_evento':('—' if ('não provada' in r['relogio_evento'] or not r['relogio_evento']) else lst(r['relogio_evento'])),'fonte':r['fonte']+('; ligação não provada a '+lst(r['relogio_evento']) if 'não provada' in r['relogio_evento'] else '')})
conv('13-misterios-limites.csv','misterio.txt',{'mist_id':'id'},['id','reino','nome','fatos_confirmados','hipoteses_abertas','a_verdade_nao_pode_ser','evidencia_para_resolver','entidades','relogio_evento','fonte'])
conv('14-resumos-de-contexto.csv','resumo.txt',{},['id','reino','tipo','nome','resumo','vizinhanca','camada_max','fonte'])
conv('13-reputacao.csv','reputacao.txt',{'rep_id':'id'},['id','reino','entidade','ganha_disposicao','perde_disposicao','quem_reage','canais','fonte'],lambda r:{'canais':r['canais'] or '?'})
conv('13-canais.csv','canal.txt',{'canal_id':'id'},['id','reino','nome','funcao','o_que_o_canone_diz','limite','entidades','fonte'],lambda r:{'entidades':r['entidades'] or '?'})
conv('15-cenas-de-teste.csv','cena.txt',{'cena_id':'id'},['id','reino','titulo','entra_ids','caracteres','nao_entra','fonte'],lambda r:{'fonte':'15-cenas-de-teste.md (R2-6.3)'})
# povo_no_reino
rows=[]
for l in txt('10-matriz-povos-nativos-cidades-e-forasteiros.md').split('\n'):
    m=re.match(r'\|\s*\d+\s*\|\s*([^|]+?)\s*\|\s*`([^`]+)`[^|]*\|\s*([^|]+?)\s*\|',l)
    if m:
        cs=re.sub(r'\s*\(\d+\)$','',m.group(3)); mp={byid[c]['nome'].split(' -- ')[0].split(' — ')[0]:c for c in cities}
        ids=[mp.get(x.strip(),'?') for x in cs.split(',')]
        rows.append(dict(povo=m.group(2),reino=K,relacao='originário',cidades=';'.join(ids),nota=m.group(1),fonte='doc 10 §1; M-05 (D-24)'))
wr('povo_no_reino.txt',['povo','reino','relacao','cidades','nota','fonte'],rows)
wr('excecoes.txt',['regra','entidade','motivo','fonte'],[dict(regra='V-A1',entidade='—',motivo='tríade não adotada como regra do reino 2 (D-33)',fonte='doc 01 parte 2')]) if False else None
EX=[('V-10',e,'a rota citada é ligação do doc 07 sem dois locais existentes (grupo de cidades, trecho fora do reino ou destino sem nome); não vira rota.txt e não ganha duração','doc 07 §2; R2-7.1') for e in ['event.assento-vazio','event.cais-fechado','event.comporta-cede','event.nevasca-fecha-passos','event.registro-duplo','event.registro-provado']]
EX.append(('V-21','route.altavento-pedra-ata','mesmo par de route.concilio-altavento-pedra-ata, mas outra natureza (mensageiros carregam cópias, não delegação); meio desconhecido nas duas','doc 07 §2; R2-7.1'))
wr('excecoes.txt',['regra','entidade','motivo','fonte'],[dict(regra=a,entidade=b,motivo=c,fonte=d) for a,b,c,d in EX])
open(os.path.join(O,'ids_aposentados.txt'),'w',encoding='utf-8',newline='\n').write('# esquema=1\nid|motivo|fonte\n')
print('ok',sorted(os.listdir(O)))
