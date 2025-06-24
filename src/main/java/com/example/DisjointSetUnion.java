package com.example;

/**
 * DSU implementation
 */
public class DisjointSetUnion {
    private final int[] parent; 
    private final int[] size;   // Эвристика 

    public DisjointSetUnion(int n) {
        parent = new int[n];
        size = new int[n];
        for (int i = 0; i < n; i++) {
            parent[i] = i; 
            size[i] = 1;   
        }
    }

    // Ассимптотика O( log*(n) ) (на самом деле обратная функция Аккермана)
    public int find(int i) {
        if (parent[i] == i) {
            return i;
        }
        return parent[i] = find(parent[i]);
    }

    // Ассимптотика O( log*(n) ) (на самом деле обратная функция Аккермана)
    public void union(int i, int j) {
        int rootI = find(i);
        int rootJ = find(j);
        if (rootI != rootJ) {
            if (size[rootI] < size[rootJ]) {
                parent[rootI] = rootJ;
                size[rootJ] += size[rootI];
            } else {
                parent[rootJ] = rootI;
                size[rootI] += size[rootJ];
            }
        }
    }
}