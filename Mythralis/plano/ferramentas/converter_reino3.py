# Conversor R3-7.1: CSVs e docs do reino 3 -> dados/*.txt (ESQUEMA_DE_DADOS.md, D-21 A). Só reorganiza; não cria fato.
import csv,re,os,json
B=os.path.join(os.path.dirname(os.path.abspath(__file__)),'..','..')
D=os.path.join(B,'reinos','03-veu-dos-malditos'); O=os.path.join(D,'dados'); os.makedirs(O,exist_ok=True)
K='kingdom.veu-dos-malditos'
def rd(f): return list(csv.DictReader(open(os.path.join(D,f),encoding='utf-8')))
def esc(v): return str(v).replace('\\','\\\\').replace('|','\\|').replace('\r','').replace('\n',' ').strip() or '?'
def wr(name,cols,rows):
    with open(os.path.join(O,name),'w',encoding='utf-8',newline='\n') as f:
        f.write('# esquema=1\n'+'|'.join(cols)+'\n')
        for r in rows: f.write('|'.join(esc(r.get(c,'?')) for c in cols)+'\n')
def lst(s):
    ids=re.findall(r'(?:npc|city|faction|settlement|clock|event|route)\.[a-z0-9-]+',s)
    return ';'.join(dict.fromkeys(ids)) if ids else ('?' if not s.strip() or s.strip().startswith('desconhecid') else ';'.join(x.strip() for x in s.split(';') if x.strip()))
txt=lambda f:open(os.path.join(D,f),encoding='utf-8').read()
REG=rd('12-registro-mestre.csv'); byid={r['id']:r for r in REG}
doc1=txt('01-reino-do-veu-dos-malditos.md'); doc10=txt('10-matriz-povos-nativos-cidades-e-forasteiros.md')
cities=[r['id'] for r in REG if r['tipo']=='cidade']
short={byid[c]['nome'].split(' — ')[0]:c for c in cities}
povo={}  # nome -> id do catálogo
for l in doc10.split('\n'):
    m=re.match(r'\|\s*\d+\s*\|\s*([^|]+?)\s*\|\s*`([^`]+)`',l)
    if m: povo[m.group(1)]=m.group(2)
    m=re.match(r'\|\s*`([^`]+)`\s*\|\s*([^|]+?)\s*\|\s*([^|]+?)\s*\|',l)
    if m and m.group(1) in {r['povo_id'] for r in REG if r['povo_id']} | {'humano','pequenino','kobold','tortle','goblin','homem-raposa','draconato','feerico','fungal','tabaxi','homem-corvo','elemental-fogo','orcano','minotauro','homem-lobo','plantae','insetoide'}: povo[m.group(2)]=m.group(1)
cp={}
for l in doc10.split('\n'):
    m=re.match(r'\|\s*(Morrovéu|Lamaluz|Fozbreu|Beira-Sal|Selovale|Borda-Mansa|Fio-Longo|Candeia-Morta)\s*\|\s*([^|]+?)\s*\|\s*([^|]+?)\s*\|',l)
    if m: cp[short[m.group(1)]]=[povo[x.strip().split(' (')[0]] for x in (m.group(2)+';'+m.group(3)).split(';') if x.strip().split(' (')[0] in povo]
# reino e calendário
wr('reino.txt',['id','nome','mundo','tema','estado','capital','calendario','fonte'],[dict(id=K,nome='Véu dos Malditos',mundo='mundo.mythralis',tema='reino de neblina, charcos e pactos, sob um rei conquistado em batalha',estado='em_composicao',capital='city.morroveu',calendario='calendario.contagem-das-provacoes',fonte='D-66; capital = sede da Corte (doc 01 V-01)')])
wr('calendario.txt',['id','meses','dias_por_mes','dias_de_transicao','estacoes','ano_inicial','estacao_inicial','fonte'],[dict(id='calendario.contagem-das-provacoes',meses=12,dias_por_mes=30,dias_de_transicao=5,estacoes='Névoa Fechada;Névoa Solta;Charco Aberto;Névoa Voltando',ano_inicial='389 das Provações (= 742 de Aeltharyn, ASSUMINDO)',estacao_inicial='Névoa Fechada',fonte='doc 01 parte 5 (D-82); mundo/M-02 §7')])
# recursos (doc 08 §3 e §5; qualitativo, D-63)
doc8=txt('08-registro-operacional-de-recursos.md'); rec=[]; seen=set(); chave={}
def addrec(nome,loc,papel,nota,fonte):
    k=(nome,loc)
    if k in seen: return
    seen.add(k); rec.append(dict(recurso=nome,localidade=loc,papel=papel,producao='?',reserva='?',demanda='?',estado_evidencia='documentado',nota=nota,fonte=fonte))
for l in doc8.split('\n'):
    m=re.match(r'\|\s*`(city\.[a-z-]+)`\s*\|\s*([^|]*)\|\s*([^|]*)\|\s*([^|]*)\|\s*([^|]*)\|\s*([^|]*)\|',l)
    if not m or m.group(1) not in byid: continue
    c,rica,pobre,dep,est,fo=[x.strip() for x in m.groups()]
    for t in [x.strip() for x in rica.split(';') if x.strip() and x.strip()!='—']:
        addrec(t,c,'produtor','rica em (qualitativo, sem quantidade)',fo); chave.setdefault(c,[]).append(t)
    for t in [x.strip() for x in dep.split(';') if x.strip() and x.strip()!='—']:
        addrec('dependência: '+t,c,'consumidor','depende de (qualitativo, sem prazo)',fo)
    for t in [x.strip() for x in pobre.split(';') if x.strip() and x.strip()!='—']:
        addrec('falta: '+t,c,'?','pobre em (só onde o doc 01 registra falta)',fo)
for l in doc8.split('\n'):
    m=re.match(r'\|\s*(Descida do Argente|Rota do Sal)[^|]*\|',l)
    if m: addrec(m.group(1),'city.fozbreu' if m.group(1).startswith('Descida') else 'city.beira-sal','transportador','fluxo qualitativo (doc 08 §5)','doc 08 §5; M-05 §5')
addrec('cuidado dos mortos','city.candeia-morta','produtor','doc 08 §3 ("cuidado dos mortos e das despedidas")','doc 08 §3')
addrec('pilotagem da barra','city.fozbreu','produtor','doc 08 §4 DR-05','doc 08 §4')
addrec('guarda de quem chega','city.borda-mansa','produtor','doc 08 §3','doc 08 §3')
wr('recurso.txt',['recurso','localidade','papel','producao','reserva','demanda','estado_evidencia','nota','fonte'],rec)
# cidades
dem={r['id']:r for r in rd('12-demografia.csv')}; asn={r['id']:r for r in rd('12-assentamentos.csv')}
res={r['id']:r for r in rd('14-resumos-de-contexto.csv')}
def fld(s,k):
    m=re.search(r'\*\*'+re.escape(k)+r':\*\*\s*(.*?)(?:\s{2}\n|\n|$)',s); return m.group(1).strip() if m else '?'
facid={}
for r in REG:
    if r['tipo'].startswith('fac'): facid[r['nome']]=r['id']
secs=re.split(r'(?=^### 2\.\d+ )',doc1,flags=re.M); rows=[]
for s in secs:
    m=re.search(r'\*\*ID:\*\*\s*`([^`]+)`',s)
    if not m or not s.startswith('### 2.'): continue
    cid=m.group(1); fac=[facid[x.strip()] for x in fld(s,'Facções presentes').split('(')[0].split(';') if x.strip() in facid]
    rows.append(dict(id=cid,reino=K,nome=byid[cid]['nome'],tipo=fld(s,'Tipo').rstrip('.'),populacao_faixa=dem[cid]['faixa_uso'],localizacao=fld(s,'Localização'),governo=fld(s,'Governo local'),governante=asn[cid]['lideranca_id'],economia=fld(s,'Economia'),defesas=fld(s,'Defesas e vulnerabilidades'),locais=fld(s,'Locais notáveis'),problemas=fld(s,'Problemas atuais'),faccoes=';'.join(fac) or '?',rumores='[rumor] '+fld(s,'Rumores (não confirmados)'),lenda=fld(s,'Lenda'),funcao=fld(s,'Função narrativa'),povos=';'.join(dict.fromkeys(cp[cid])),acesso='?',recurso_chave=';'.join(chave.get(cid,[])) or '?',fonte='doc 01 '+byid[cid]['ficha_doc']+'; proposto (D-68)'))
wr('cidade.txt',list(rows[0].keys()),rows)
wr('assentamento.txt',['id','reino','cidade_mae','nome','tipo','populacao_faixa','lideranca','obrigacao','interesse_local','tensao','acesso','fonte'],[])
# npcs (papéis de tríade: doc 01 parte 2 §3)
nome2npc={r['nome']:r['id'] for r in REG if r['tipo']=='npc'}; TRI={}
for l in doc1.split('\n'):
    m=re.match(r'\|\s*(Morrovéu|Lamaluz|Fozbreu|Beira-Sal|Selovale|Borda-Mansa|Fio-Longo|Candeia-Morta)\s*\|([^|]+)\|([^|]+)\|([^|]+)\|',l)
    if m:
        for col,rl in zip(m.groups()[1:],('heroi','guardiao','benfeitor')):
            n=re.sub(r'\s*\(.*?\)|\*','',col).strip()
            if n in nome2npc: TRI.setdefault(nome2npc[n],[]).append(rl)
comp={r['id']:r for r in rd('12-competencias.csv')}; fich={r['id']:r for r in rd('12-fichas.csv')}; voz={r['npc_id']:r for r in rd('12-voz.csv')}
rows=[]
for r in REG:
    if r['tipo']!='npc': continue
    i=r['id']; c=comp[i]; f=fich.get(i); v=voz.get(i); p=r['papel']; ex=r['extra']; pap=[]
    if 'lendário' in ex: pap=['heroi']
    else:
        if 'governante' in ex: pap.append('governante')
        if 'general' in ex: pap.append('general')
        if 'herói vivo' in ex: pap.append('heroi')
        if re.search(r'guardi[ãa]o|guardiã',p,re.I) : pap.append('guardiao')
        if re.search(r'benfeit',p,re.I): pap.append('benfeitor')
        for rl in TRI.get(i,[]):
            if rl not in pap: pap.append(rl)
        if not pap: pap=['outro']
    fm=re.search(r'facção:\s*(?!nenhuma)([a-z0-9-]+)',f['cidade_faccao']) if f else None
    sub=c['subraca_id']; sub='—' if sub in('--','') else sub
    rows.append(dict(id=i,reino=K,nome=r['nome'],idade=(f['idade'] if f else '?'),povo=c['raca_id'],subraca=sub,classe=c['classe_id'],profissao=c['profissao_id'],cidade=r['cidade'] or '—',faccao=('faction.'+fm.group(1)) if fm else '—',cargo=(f['cargo'] if f else p),papeis=';'.join(pap),principal='sim' if f else 'não',vivo=r['vivo'],personalidade=(f['personalidade'] if f else '?'),objetivo=(f['agenda'] if f else '?'),limites=c['limites'],capacidades=c['capacidades'],agenda=(f['agenda'] if f else '?'),pressao=(f['pressao'] if f else '?'),tom=(v['tom'] if v else '?'),resumo=res[i]['resumo'],fonte='doc 01 parte 2; 12-competencias.csv; proposto ('+r['situacao']+'); objetivo = agenda (ASSUMINDO)'))
wr('npc.txt',list(rows[0].keys()),rows)
# facções
rows=[]
for r in rd('12-facoes.csv'):
    rows.append(dict(id=r['id'],reino=K,nome=r['nome'],lideranca=r['lideranca_id'],objetivo=r['objetivo'],recursos=r['recursos'],metodos=r['metodos'],tensao=r['tensao'],aliados=lst(r['aliados']) if 'faction.' in r['aliados'] else '?',rivais=lst(r['rivais']) if 'faction.' in r['rivais'] else '?',presenca_cidades=lst(r['presenca_cidades']),presenca_assentamentos='—',fonte='doc 04 '+byid[r['id']]['ficha_doc']+'; proposto (D-71)'))
wr('faccao.txt',list(rows[0].keys()),rows)
# rotas: só ligações entre dois locais existentes (doc 07 §2)
rows=[]
def rota(i,a,b,cond,fonte): rows.append(dict(id=i,reino=K,de=a,para=b,meio='?',duracao_min='?',duracao_max='?',unidade='dia',estado='desconhecida',condicao=cond,fonte=fonte))
for c in cities:
    if c!='city.morroveu': rota('route.conselho-'+c.split('.')[1]+'-morroveu',c,'city.morroveu','assento no Conselho dos Pactuários; duração desconhecida','doc 07 route.conselho-morroveu')
for i,a,b in [('route.lamaluz-fozbreu','city.lamaluz','city.fozbreu'),('route.morroveu-selovale','city.morroveu','city.selovale'),('route.morroveu-fozbreu','city.morroveu','city.fozbreu')]:
    rota(i,a,b,'documentada ou sugerida por dependência; sem percurso descrito; duração desconhecida','doc 07 '+i)
wr('rota.txt',['id','reino','de','para','meio','duracao_min','duracao_max','unidade','estado','condicao','fonte'],rows)
# relógios
rows=[]
for r in rd('12-relogios.csv'):
    a,b=r['progresso_inicial'].split('/')
    rows.append(dict(id=r['clock_id'],reino=K,nome=r['nome'],segmentos=b,progresso=a,move=r['o_que_move'],trava=r['o_que_trava'],paga=r['quem_paga'],completa=r['ao_completar'],inacao=r['efeito_da_inacao'],entidades=lst(r['entidades']),fonte=r['fonte']))
wr('relogio.txt',list(rows[0].keys()),rows)
wr('relacao.txt',['origem','destino','destino_texto','tipo','descricao','fonte','certeza'],[dict(origem=r['origem_id'],destino=r['destino_id'],destino_texto=r['destino_texto'],tipo=r['tipo'],descricao=r['descricao'],fonte=r['fonte'],certeza=r['certeza']) for r in rd('12-relacoes.csv')])
PROG={r['clock_id']:r['progresso_inicial'] for r in rd('12-relogios.csv')}
def conv(src,name,ren,cols,extra=None):
    rows=[]
    for r in rd(src):
        x={ren.get(k,k):v for k,v in r.items()}; x['reino']=K
        if extra: x.update(extra(r))
        rows.append(x)
    wr(name,cols,rows)
conv('14-camadas-de-informacao.csv','fato.txt',{'fact_id':'id'},['id','reino','entidade','fato','camada','estado','quem_sabe','evidencia','caminho','como_descobrir','marcador','fonte'],lambda r:{'marcador':r['marcador'] or (('progresso inicial '+PROG[r['entidade']]) if r['entidade'] in PROG else '—')})
conv('12-eventos.csv','evento.txt',{'evento_id':'id','cidades':'cidades_e_assentamentos'},['id','reino','nome','gatilho','relogio','cadeia','cidades_e_assentamentos','recursos','pessoas_e_faccoes','rotas','fonte'])
conv('13-sementes-de-aventura.csv','semente.txt',{'seed_id':'id'},['id','reino','entidade','porte','ameaca','titulo','gancho','agentes','abordagens_possiveis','se_ninguem_age','relogio_evento','fonte'])
conv('13-misterios-limites.csv','misterio.txt',{'mist_id':'id'},['id','reino','nome','fatos_confirmados','hipoteses_abertas','a_verdade_nao_pode_ser','evidencia_para_resolver','entidades','relogio_evento','fonte'])
conv('14-resumos-de-contexto.csv','resumo.txt',{},['id','reino','tipo','nome','resumo','vizinhanca','camada_max','fonte'])
conv('13-reputacao.csv','reputacao.txt',{'rep_id':'id'},['id','reino','entidade','ganha_disposicao','perde_disposicao','quem_reage','canais','fonte'],lambda r:{'canais':r['canais'] or '?'})
conv('13-canais.csv','canal.txt',{'canal_id':'id'},['id','reino','nome','funcao','o_que_o_canone_diz','limite','entidades','fonte'],lambda r:{'entidades':r['entidades'] or '?'})
conv('15-cenas-de-teste.csv','cena.txt',{'cena_id':'id'},['id','reino','titulo','entra_ids','caracteres','nao_entra','fonte'],lambda r:{'fonte':'15-cenas-de-teste.md (R3-6.3)'})
# povo_no_reino (11 nativos, doc 10 §1)
rows=[]
for l in doc10.split('\n'):
    m=re.match(r'\|\s*\d+\s*\|\s*([^|]+?)\s*\|\s*`([^`]+)`\s*\|\s*([^|]+?)\s*\|',l)
    if m: rows.append(dict(povo=m.group(2),reino=K,relacao='originário',cidades=';'.join(short[x.strip()] for x in m.group(3).split(',')),nota=m.group(1),fonte='doc 10 §1; M-05 (D-24)'))
wr('povo_no_reino.txt',['povo','reino','relacao','cidades','nota','fonte'],rows)
# exceções: eventos que citam rotas do doc 07 sem dois locais existentes
EXR={'route.conselho-morroveu':'grupo de sete ligações (cada lugar ↔ Morrovéu); vive em rota.txt como sete rotas','route.fozbreu-argente':'trecho rio acima, fora do reino','route.fozbreu-cabotagem':'cabotagem no Mar Austral, sem destino nomeado','route.beira-sal-ermo':'Ermo de Sal, fora do reino'}
EX=[]
for r in rd('12-eventos.csv'):
    for t in re.findall(r'route\.[a-z0-9-]+',r['rotas']):
        if t in EXR: EX.append(('V-10',r['evento_id'],'a rota citada é ligação do doc 07 sem dois locais existentes ('+EXR[t]+'); não vira rota.txt e não ganha duração','doc 07 §2; R3-7.1'))
EX+=[('V-21',i,'mesmo par de route.conselho-'+i.split('-')[1]+'-morroveu, mas outra natureza (elo por facção ou dependência, não assento no Conselho); meio desconhecido nas duas','doc 07 §2; R3-7.1') for i in ['route.morroveu-selovale','route.morroveu-fozbreu']]
wr('excecoes.txt',['regra','entidade','motivo','fonte'],[dict(regra=a,entidade=b,motivo=c,fonte=d) for a,b,c,d in EX])
open(os.path.join(O,'ids_aposentados.txt'),'w',encoding='utf-8',newline='\n').write('# esquema=1\nid|motivo|fonte\n')
print('ok',sorted(os.listdir(O)))
