/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package TreeStructure;

import java.util.*;

/**
 *
 * @author Ankhbayar
 */
public class BreadthFirstTreeIterator implements Iterator<Node> {

    private static final int ROOT = 0;

    private LinkedList<Node> list;
    private HashMap<Integer, ArrayList<String>> levels;

    public BreadthFirstTreeIterator(HashMap<String, Node> tree, String data) {
        list = new LinkedList<Node>();
        levels = new HashMap<Integer, ArrayList<String>>();

        if (tree.containsKey(data)) {
            this.buildList(tree, data, ROOT);

            for (Map.Entry<Integer, ArrayList<String>> entry : levels.entrySet()) {
                for (String child : entry.getValue()) {
                    list.add(tree.get(child));
                }
            }
        }
    }

    private void buildList(HashMap<String, Node> tree, String data, int level) {
        if (level == ROOT) {
            list.add(tree.get(data));
        }

        ArrayList<String> children = tree.get(data).getChildren();

        if (!levels.containsKey(level)) {
            levels.put(level, new ArrayList<String>());
        }
        for (String child : children) {
            levels.get(level).add(child);

            // Recursive call
            this.buildList(tree, child, level + 1);
        }
    }

    @Override
    public boolean hasNext() {
        return !list.isEmpty();
    }

    @Override
    public Node next() {
        return list.poll();
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}