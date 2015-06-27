def PersonalRank(G, alpha, root):
    rank = {x:0 for x in G.keys()}
    rank[root]=1
    
    for i in range(20):
        tmp = {x:0 for x in G.keys()}
        for node,rk in rank.items():
            paths=G[node]
            for path in paths:
                tmp[path] += alpha*rk/len(paths)
            
        tmp[root] += (1-alpha)
        rank = tmp
    
    return rank

record=[('A','a'),('A','b'),('B','a'),('B','c'),('C','b')]
g = {}
for rec in record:
    if rec[0] not in g:
        g[rec[0]]=set()
    g[rec[0]].add(rec[1])
    
    if rec[1] not in g:
        g[rec[1]]=set()
    g[rec[1]].add(rec[0])
    
print PersonalRank(g,0.9,'A')