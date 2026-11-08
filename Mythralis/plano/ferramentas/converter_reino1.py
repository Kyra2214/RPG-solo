# Conversor F1 (Mythralis): CSVs e docs do reino 1 (Aeltharyn) -> dados/*.txt, no MESMO esquema=1 dos reinos 2 a 5
# (ESQUEMA_DE_DADOS.md). Só reorganiza; não cria cânone. Tudo que o conversor DERIVA (e não copia) fica no campo `fonte`.
import csv,re,os,json,unicodedata
B=os.path.join(os.path.dirname(os.path.abspath(__file__)),'..','..')
D=os.path.join(B,'reinos','01-aeltharyn-reino-das-florestas'); O=os.path.join(D,'dados'); os.makedirs(O,exist_ok=True)
K='kingdom.aeltharyn'
def rd(f): return list(csv.DictReader(open(os.path.join(D,f),encoding='utf-8')))
def esc(v): return str(v).replace('\\','\\\\').replace('|','\\|').replace('\r','').replace('\n',' ').strip() or '?'
def wr(name,cols,rows):
    with open(os.path.join(O,name),'w',encoding='utf-8',newline='\n') as f:
        f.write('# esquema=1\n'+'|'.join(cols)+'\n')
        for r in rows: f.write('|'.join(esc(r.get(c,'?')) for c in cols)+'\n')
def norm(s): return ''.join(c for c in unicodedata.normalize('NFD',s.lower()) if unicodedata.category(c)!='Mn')
def lst(s):
    ids=re.findall(r'(?:npc|city|faction|settlement|clock|event|route)\.[a-z0-9-]+',s)
    return ';'.join(dict.fromkeys(ids)) if ids else ('?' if not s.strip() else ';'.join(x.strip() for x in s.split(';') if x.strip()))
txt=lambda f:open(os.path.join(D,f),encoding='utf-8').read()
REG=rd('12-registro-mestre.csv'); byid={r['id']:r for r in REG}
doc1=txt('01-reino-das-florestas.md'); doc10=txt('10-matriz-povos-nativos-cidades-e-forasteiros.md')
DEM={r['id']:r for r in rd('12-demografia.csv')}
def num(s):
    m=re.match(r'[\d.]+',s.strip()); return int(m.group(0).replace('.','')) if m else None
def faixa(i):
    n=num(DEM[i]['valor_perfil']) if i in DEM else None
    if n is None: return 'indefinida'
    return 'pequena' if n<10000 else 'média' if n<30000 else 'grande'
# ---------- reino e calendário
wr('reino.txt',['id','nome','mundo','tema','estado','capital','calendario','fonte'],[dict(id=K,nome='Aeltharyn',mundo='mundo.mythralis',tema='Reino das Florestas, governado pela Coroa Élfica e por pactos com povos aliados',estado='em_composicao',capital='city.elarion',calendario='calendario.contagem-das-seivas',fonte='doc 01 §1-2; doc 10 §1 (Aeltharyn é reino, não cidade); doc 11 (fechado editorialmente; em refinamento)')])
wr('calendario.txt',['id','meses','dias_por_mes','dias_de_transicao','estacoes','ano_inicial','estacao_inicial','fonte'],[dict(id='calendario.contagem-das-seivas',meses=12,dias_por_mes=30,dias_de_transicao=5,estacoes='Silêncio;Despertar;Plenitude;Queda',ano_inicial='742 das Seivas',estacao_inicial='Silêncio',fonte='doc 01 §35 (D-14 a D-16); mundo/M-02 §1. Ordem das estações: o ano começa em Silêncio')])
# ---------- recursos
RC=rd('12-recursos.csv')
def prole(p):
    w=p.split(' ')[0].strip(';,').lower()
    return {'produtor':'produtor','consumidor':'consumidor','transportador':'transportador'}.get(w,'?')
rec=[dict(recurso=r['recurso'],localidade=(K if r['localidade']=='reino' else r['localidade']),papel=prole(r['papel']),producao=r['producao'],reserva=r['reserva'],demanda=r['demanda'],estado_evidencia=r['estado_evidencia'],nota=(r['papel']+'; '+r['nota']) if r['nota'] else r['papel'],fonte=r['fonte']) for r in RC]
wr('recurso.txt',['recurso','localidade','papel','producao','reserva','demanda','estado_evidencia','nota','fonte'],rec)
chave={}
for r in rec:
    if r['papel']=='produtor' and r['localidade'].startswith('city.'): chave.setdefault(r['localidade'],[]).append(r['recurso'])
# ---------- povos (nome/adjetivo -> ID do catálogo)
PV=[('élfic|elfo|elfa','elfo'),('feéric','feerico'),('gnomo','gnomo'),('pequenin','pequenino'),('corvári','homem-corvo'),('silvano|sátiro','satiro'),('felídeo','tabaxi'),('centauro','centauro'),('orcano','orcano'),('micélio','fungal'),('verdejante','plantae'),('anõ|anao','anao'),('human','humano'),('goblin','goblin'),('ratoide','homem-rato'),('abissal|tritã|tritõ','tritao')]
def povos_de(s):
    s=s.lower(); return [i for pat,i in PV if re.search(pat,s)]
# ---------- facções (nomes -> ids)
FAC=rd('12-facoes.csv'); fname={norm(f['nome']):f['id'] for f in FAC}
lead={f['lideranca_id']:f['id'] for f in FAC}
nome2npc={r['nome']:r['id'] for r in REG if r['tipo']=='npc'}
# ---------- cidades
secs=re.split(r'(?=^### 5\.\d+ )',doc1,flags=re.M)
def fld(s,*ks):
    for k in ks:
        m=re.search(r'\*\*'+re.escape(k)+r':\*\*\s*(.*?)(?:\s{2}\n|\n|$)',s)
        if m: return m.group(1).strip()
    return '?'
res={r['id']:r for r in rd('14-resumos-de-contexto.csv')}
cities=[]
for s in secs:
    m=re.search(r'\*\*ID:\*\*\s*`([^`]+)`',s)
    if not m or not s.startswith('### 5.'): continue
    cid=m.group(1); sec=re.match(r'### (5\.\d+)',s).group(1); nome=byid[cid]['nome']; curto=re.split(r' — ',nome)[0]
    linha10=' '.join(l for l in doc10.split('\n') if l.startswith('|') and curto in l.split('|')[1])
    pv=povos_de(fld(s,'População')+' '+linha10)
    gov=fld(s,'Governo local','Governo'); g=[v for k,v in nome2npc.items() if k in gov]
    fac=[fid for k,fid in fname.items() if k in norm(fld(s,'Facções presentes','Facções'))]
    cities.append(dict(id=cid,reino=K,nome=nome,tipo=fld(s,'Tipo').rstrip('.'),populacao_faixa=faixa(cid),localizacao=fld(s,'Localização'),governo=gov,governante=g[0] if g else '?',economia=fld(s,'Economia'),defesas=fld(s,'Defesas'),locais=fld(s,'Locais notáveis','Locais'),problemas=fld(s,'Problemas atuais','Problemas'),faccoes=';'.join(fac) or '?',rumores='[rumor] '+fld(s,'Rumores','Rumores (não confirmados)'),lenda=fld(s,'Lenda'),funcao=fld(s,'Função narrativa','Gancho'),povos=';'.join(pv),acesso='?',recurso_chave=';'.join(chave.get(cid,[])) or '?',fonte='doc 01 §%s; doc 10 §3; faixa derivada da estimativa narrativa (<10.000 pequena; <30.000 média; demais grande); proposto (F1)'%sec))
# ---------- rotas (tabela transcrita de doc 07 §2 e §3; não se inventa duração)
E='doc 07 '
ROTAS=[ # id, de, para, meio, min, max, estado, condição, fonte
('route.elarion-nymvar','elarion','nymvar','misto',1,2,'com_ressalva','estrada 2 dias; rio 1–2 dias conforme corrente; usar o intervalo fluvial',E+'§2'),
('route.elarion-vael-tir','elarion','vael-tir','trilha',3,3,'com_ressalva','3 dias; mais com quarentena florestal; sem duração máxima fixada',E+'§2'),
('route.elarion-caladwen','elarion','caladwen','misto',1.5,2,'documentada','estrada 2 dias; trilhas rápidas 1,5 dia (exige trilhas e conhecimento)',E+'§2'),
('route.elarion-arven','elarion','arven','estrada',1.5,1.5,'documentada','sem modificadores de estação definidos',E+'§2'),
('route.nymvar-orvass','nymvar','orvass','misto',2,6,'com_ressalva','barco e passarelas; referência normal 3 dias; 2–6 com desvios, cheia ou canais alterados',E+'§2'),
('route.nymvar-arven','nymvar','arven','misto',1,4,'com_ressalva','caravana 2–4 dias; viajante leve 1 dia (D-12); no Despertar lama e cheias podem duplicar o prazo',E+'§2'),
('route.thalanor-elarion','thalanor','elarion','rio',3,3,'com_ressalva','barco pelo Lúmen, 3 dias; tempo de meio diferente da estrada costeira',E+'§2'),
('route.thalanor-elarion-costeira','thalanor','elarion','estrada',5,5,'com_ressalva','estrada costeira, 5 dias; id novo da conversão para separar do barco (mesmo par, outro meio)',E+'§2'),
('route.thalanor-silvaran','thalanor','silvaran','estrada',4,4,'documentada','pomares e vales; sem alternativa quantificada',E+'§2'),
('route.caladwen-brumafunda','caladwen','brumafunda','trilha',2,2,'com_ressalva','2 dias COM guia; sem guia pode demorar muito mais',E+'§2'),
('route.brumafunda-vael-tir','brumafunda','vael-tir','trilha',2,4,'com_ressalva','depende das trilhas abertas',E+'§2'),
('route.torvelume-elarion','torvelume','elarion','estrada',4,4,'documentada','estrada de fronteira; postos e bloqueios sem duração adicional definida',E+'§2'),
('route.torvelume-vael-tir','torvelume','vael-tir','estrada',1,2,'com_ressalva','trecho da Estrada da Espinha; geada, deslizamento e interdição podem fechar a passagem; sem desvio calculável',E+'§2'),
('route.torvelume-caladwen','torvelume','caladwen','estrada',3,3,'com_ressalva','risco de deslizamento na chuva',E+'§2'),
('route.nymvar-thalanor','nymvar','thalanor','rio',3,4,'com_ressalva','barco de carga do Lúmen em condições comuns; contra a corrente quase o dobro',E+'§3'),
('route.arven-silvaran','arven','silvaran','estrada',2,4,'com_ressalva','Rota dos Celeiros, intervalo do corredor por caravana; no Despertar lama e cheias podem duplicar',E+'§3'),
('route.orvass-brumafunda','orvass','brumafunda','?','?','?','desconhecida','Rota do Pântano; percurso real, navegabilidade e duração desconhecidos (decisão M2.1: permanece sem duração)',E+'§3')]
rotas=[dict(id=i,reino=K,de='city.'+a,para='city.'+b,meio=m,duracao_min=lo,duracao_max=hi,unidade='dia',estado=e,condicao=c,fonte=f) for i,a,b,m,lo,hi,e,c,f in ROTAS]
wr('rota.txt',['id','reino','de','para','meio','duracao_min','duracao_max','unidade','estado','condicao','fonte'],rotas)
for c in cities:
    ms={r['meio'] for r in rotas if c['id'] in(r['de'],r['para'])}
    agua=bool(ms&{'rio','mar'}); terra=bool(ms-{'rio','mar','?'}) or '?' in ms
    c['acesso']='ambos' if agua and terra else 'navegavel' if agua else 'terrestre'
wr('cidade.txt',list(cities[0].keys()),cities)
# ---------- assentamentos
rows=[]
for r in rd('12-assentamentos.csv'):
    g=byid[r['id']]; rows.append(dict(id=r['id'],reino=K,cidade_mae=r['cidade_mae'] if r['cidade_mae'].startswith('city.') else 'city.'+norm(r['cidade_mae']).replace(' ','-'),nome=g['nome'],tipo=DEM[r['id']]['categoria'],populacao_faixa='pequena' if (num(DEM[r['id']]['valor_perfil']) or 0)<5000 else 'média',lideranca=r['lideranca_id'],obrigacao=r['obrigacao'],interesse_local=r['interesse_local'],tensao=r['tensao'],acesso='?',fonte='doc 03; doc 07 §3.1; faixa derivada (<5.000 pequena); proposto (F1)'))
wr('assentamento.txt',list(rows[0].keys()),rows)
# ---------- NPCs
comp={r['id']:r for r in rd('12-competencias.csv')}; fich={r['id']:r for r in rd('12-fichas-elarion.csv')}; voz={r['npc_id']:r for r in rd('13-voz.csv' if os.path.exists(os.path.join(D,'13-voz.csv')) else '12-voz.csv')}
rel=rd('12-relacoes.csv')
cityname={norm(re.split(r' — ',c['nome'])[0]):c['id'] for c in cities}
def cidade_de(i,r):
    if r['cidade']:
        ids=[cityname[norm(x.strip())] for x in r['cidade'].split(',') if norm(x.strip()) in cityname]
        if ids: return ids[0]
    f=fich.get(i)
    if f:
        m=re.search(r'city\.[a-z0-9-]+',f['cidade_faccao'])
        if m: return m.group(0)
    for x in rel:
        if x['origem_id']==i and x['destino_id'].startswith('city.'): return x['destino_id']
    cg=norm(r['extra']+' '+(fich[i]['cargo'] if i in fich else ''))
    if re.search(r'rei de aeltharyn|herdeira reconhecida|conselho das sete copas',cg): return 'city.elarion'  # sede da Coroa e do Conselho (doc 01 §4-5.1)
    for k,cid in cityname.items():
        t=norm(res.get(i,{}).get('resumo',''))
        if re.search(r'\b'+re.escape(k)+r'\b',t): return cid
    return '—'
PAP={'GUARDIAN':'guardiao','BENEFACTOR':'benfeitor','HERO':'heroi'}
govs={c['governante'] for c in cities if c['governante']!='?'}
rows=[];exc=[]
for r in REG:
    if r['tipo']!='npc': continue
    i=r['id']; c=comp.get(i,{}); f=fich.get(i); v=voz.get(i)
    ctx=(r['extra']+' '+r['ficha_doc']+' '+(f['estado'] if f else '')).lower()
    morto=bool(re.search(r'falecid|morto|morta',ctx))
    cargo=f['cargo'] if f else (r['extra'] or r['papel'] or '?')
    papeis=[PAP[r['papel']]] if r['papel'] in PAP else []
    if re.search(r'\brei\b',cargo.lower()) and 'herdeir' not in cargo.lower(): papeis.append('governante')
    if 'herdeir' in cargo.lower(): papeis.append('herdeiro')
    if re.search(r'general',cargo.lower()): papeis.append('general')
    if i in govs and 'governante' not in papeis: papeis.append('governante')
    if morto and 'heroi' not in papeis and ('heróic' in ctx or 'heroína' in ctx or 'herói' in ctx): papeis.append('heroi')
    ida=(f['idade'] if f else '')
    if not ida:
        m=re.search(r'(\d+)\s*anos',r['ficha_doc']); ida=('cerca de '+m.group(1)) if m and 'cerca' in r['ficha_doc'] else (m.group(1) if m else '')
    if not ida: ida=r['ficha_doc'] or ('falecido(a)' if morto else '?')
    sub=c.get('subraca_id','—'); sub='—' if sub in('--','') else sub
    classe=c.get('classe_id','—'); classe='—' if classe in('--','') else classe
    prof=c.get('profissao_id','—'); prof='—' if prof in('--','') else prof
    povo=c.get('raca_id') or r['povo'] or '?'
    povo='?' if povo in('—','--') else povo
    if povo=='?': exc.append(('V-12',i,'povo sem base no texto de Aeltharyn (12-competencias.csv: "povo, classe e profissão sem base no texto"); lacuna explícita, não se inventa','12-competencias.csv; F1'))
    if classe=='—': exc.append(('V-12',i,'NPC sem classe no cânone de Aeltharyn (12-competencias.csv grava "—"); a classe é lacuna deliberada, não erro de dado','12-competencias.csv; F1'))
    rows.append(dict(id=i,reino=K,nome=r['nome'],idade=ida,povo=povo,subraca=sub,classe=classe,profissao=prof,ccidade='',cidade=cidade_de(i,r),faccao=lead.get(i,'—'),cargo=cargo,papeis=';'.join(dict.fromkeys(papeis)) or 'outro',principal='sim' if f else 'não',vivo='não' if morto else 'sim',personalidade=(f['personalidade'] if f else '?'),objetivo=(f['agenda'] if f else '?'),limites=c.get('limites','?'),capacidades=c.get('capacidades','?'),agenda=(f['agenda'] if f else '?'),pressao=(f['pressao'] if f else '?'),tom=(v['tom'] if v else '?'),resumo=res.get(i,{}).get('resumo','?'),fonte='doc 01; 12-competencias.csv; vivo e idade lidos do texto do registro (falecid-/morto-); cidade inferida quando o registro não a traz; proposto (F1)'))
for x in rows: x.pop('ccidade',None) if False else None
wr('npc.txt',['id','reino','nome','idade','povo','subraca','classe','profissao','cidade','faccao','cargo','papeis','principal','vivo','personalidade','objetivo','limites','capacidades','agenda','pressao','tom','resumo','fonte'],rows)
# ---------- facções (aliados e rivais só quando a prosa cita facção pelo nome; senão "?")
def nomes_fac(s,self_id):
    ids=[fid for k,fid in fname.items() if k in norm(s) and fid!=self_id]; return ';'.join(ids) or '?'
rows=[]
for f in FAC:
    rows.append(dict(id=f['id'],reino=K,nome=f['nome'],lideranca=f['lideranca_id'],objetivo=f['objetivo'],recursos=f['recursos'],metodos=f['metodos'],tensao=f['tensao'],aliados=nomes_fac(f['aliados'],f['id']),rivais=nomes_fac(f['rivais'],f['id']),presenca_cidades=lst(f['presenca_cidades']),presenca_assentamentos=lst(f['presenca_assentamentos']),fonte='doc 01 §7; doc 04; aliados e rivais só quando citam outra facção pelo nome'))
wr('faccao.txt',list(rows[0].keys()),rows)
# ---------- relógios, relações
rows=[]
for r in rd('12-relogios.csv'):
    a,b=r['progresso_inicial'].split('/')
    rows.append(dict(id=r['clock_id'],reino=K,nome=r['nome'],segmentos=b,progresso=a,move=r['o_que_move'],trava=r['o_que_trava'],paga=r['quem_paga'],completa=r['ao_completar'],inacao=r['efeito_da_inacao'],entidades=lst(r['entidades']),fonte=r['fonte']))
wr('relogio.txt',list(rows[0].keys()),rows)
TIPO={'cooperação':'aliança','negociação':'aliança','respeito':'aliança','afeto':'aliança','vínculo':'vínculo de cargo','dependência':'dívida','dívida':'dívida','ligação comercial':'aliança','rivalidade':'rivalidade','discordância':'tensão','tensão':'tensão','desconfiança':'medo'}
rows=[dict(origem=r['origem_id'],destino=r['destino_id'],destino_texto=r['destino_texto'],tipo=TIPO.get(r['tipo'],r['tipo']),descricao=('[%s] '%r['tipo'])+r['descricao'],fonte=r['fonte'],certeza='C') for r in rel]
wr('relacao.txt',['origem','destino','destino_texto','tipo','descricao','fonte','certeza'],rows)
# ---------- fatos (o CSV de Aeltharyn não tem `marcador`; deriva-se um trecho distintivo que NÃO aparece em resumo público/local)
pub=' '.join(norm(r['resumo']+' '+r['vizinhanca']) for r in res.values() if r['camada_max'] in('publico','local'))
def marcador(fato):
    w=[x for x in re.findall(r'[\wÀ-ÿ-]+',fato) if len(x)>=4]
    for n in (3,2,4,5):
        for k in range(len(w)-n+1):
            m=' '.join(w[k:k+n])
            if norm(m) not in pub: return m
    return '—'
rows=[]
for r in rd('14-camadas-de-informacao.csv'):
    x={'id':r['fact_id'],'reino':K,'entidade':r['entidade'],'fato':r['fato'],'camada':r['camada'],'estado':r['estado'],'quem_sabe':r['quem_sabe'] or '—','evidencia':r['evidencia'] or '?','caminho':r['caminho'] or '?','como_descobrir':r['como_descobrir'] or '?','marcador':marcador(r['fato']) if r['camada'] in('restrito','secreto') else '—','fonte':r['fonte']+('; marcador derivado pelo conversor' if r['camada'] in('restrito','secreto') else '')}
    rows.append(x)
wr('fato.txt',['id','reino','entidade','fato','camada','estado','quem_sabe','evidencia','caminho','como_descobrir','marcador','fonte'],rows)
# ---------- demais tipos (renomeia chaves)
def conv(src,name,ren,cols,extra=None):
    rows=[]
    for r in rd(src):
        x={ren.get(k,k):v for k,v in r.items()}; x['reino']=K
        if extra: x.update(extra(r))
        rows.append(x)
    wr(name,cols,rows)
conv('12-eventos.csv','evento.txt',{'evento_id':'id'},['id','reino','nome','gatilho','relogio','cadeia','cidades_e_assentamentos','recursos','pessoas_e_faccoes','rotas','fonte'])
conv('13-sementes-de-aventura.csv','semente.txt',{'seed_id':'id'},['id','reino','entidade','porte','ameaca','titulo','gancho','agentes','abordagens_possiveis','se_ninguem_age','relogio_evento','fonte'],lambda r:{'relogio_evento':('—' if ('não provada' in r['relogio_evento'] or not r['relogio_evento']) else lst(r['relogio_evento']))})
conv('13-misterios-limites.csv','misterio.txt',{'mist_id':'id'},['id','reino','nome','fatos_confirmados','hipoteses_abertas','a_verdade_nao_pode_ser','evidencia_para_resolver','entidades','relogio_evento','fonte'])
conv('14-resumos-de-contexto.csv','resumo.txt',{},['id','reino','tipo','nome','resumo','vizinhanca','camada_max','fonte'])
conv('13-reputacao.csv','reputacao.txt',{'rep_id':'id'},['id','reino','entidade','ganha_disposicao','perde_disposicao','quem_reage','canais','fonte'],lambda r:{'canais':r['canais'] or '?'})
conv('13-canais.csv','canal.txt',{'canal_id':'id'},['id','reino','nome','funcao','o_que_o_canone_diz','limite','entidades','fonte'],lambda r:{'entidades':r['entidades'] or '?'})
conv('15-cenas-de-teste.csv','cena.txt',{'cena_id':'id'},['id','reino','titulo','entra_ids','caracteres','nao_entra','fonte'],lambda r:{'fonte':'15-cenas-de-teste.md'})
# ---------- povo no reino (11 nativos do doc 10 §2 + aliado costeiro + 4 residentes F*)
NAT=['elfo','satiro','feerico','plantae','fungal','centauro','tabaxi','orcano','homem-corvo','pequenino','gnomo']
cmap={}
for c in cities:
    for p in c['povos'].split(';'): cmap.setdefault(p,[]).append(c['id'])
rows=[dict(povo=p,reino=K,relacao='originário',cidades=';'.join(cmap.get(p,[])) or '?',nota=['Elfos','Silvanos Festivos (sátiros)','Feéricos','Verdejantes','Micélioides','Centauros das Florestas','Felídeos das Selvas','Orcanos das Matas','Corvários','Pequeninos','Gnomos dos Vales'][NAT.index(p)],fonte='doc 10 §2; M-05 §3 (D-24); cidades por menção em doc 01 §5 e doc 10 §3') for p in NAT]
rows.append(dict(povo='tritao',reino=K,relacao='aliado',cidades=';'.join(cmap.get('tritao',[])) or '?',nota='Abissal Costeiro (aliado costeiro, não nativo)',fonte='doc 10; M-05 §3 (D-24)'))
for p,n in [('humano','Humano'),('anao','Anão'),('goblin','Goblin'),('homem-rato','Ratoide')]:
    rows.append(dict(povo=p,reino=K,relacao='migrante',cidades=';'.join(cmap.get(p,[])) or '?',nota=n+' (residente ou viajante; não nativo)',fonte='doc 01 §2.4; doc 10 §1; M-05 M5-04 (proposto, F*)'))
wr('povo_no_reino.txt',['povo','reino','relacao','cidades','nota','fonte'],rows)
exc+=[('V-13','event.ponte-cede','o evento cita figuras históricas mortas (Maereth, cartógrafa das Copas, e Avarra das Sete Pontes) como precedente e lenda da ponte, não como agentes vivos','doc 01; 12-eventos.csv; F1')]
wr('excecoes.txt',['regra','entidade','motivo','fonte'],[dict(regra=a,entidade=b,motivo=c,fonte=d) for a,b,c,d in exc])
open(os.path.join(O,'ids_aposentados.txt'),'w',encoding='utf-8',newline='\n').write('# esquema=1\nid|motivo|fonte\n')
print('ok',sorted(os.listdir(O)))
