package com.javarush.task.task20.task2028;

import java.io.IOException;
import java.io.Serializable;
import java.util.*;

/* 
Построй дерево(1)
*/
public class CustomTree<T> extends AbstractList<T> implements Cloneable, Serializable {
    Entry<T> root;

    public CustomTree() {
        this.root = new Entry<>(null);
    }

    @Override
    public boolean add(T elem) {
        ElementAdder<T> adder = new ElementAdder<>(elem);
        root.inspectWidth(adder, true);
        if (adder.isAdded) return true;
        ForcedElementAdder<T> forcedAdder = new ForcedElementAdder<>(elem);
        root.inspectWidth(forcedAdder, true);
        return forcedAdder.isAdded;
    }

    @Override
    public int size() {
        SizeReviser reviser = new SizeReviser<>();
        root.inspectDepth(reviser, false);
        return reviser.size;
    }

    public T getParent(T elem) {
        ParentSearcher<T> searcher = new ParentSearcher<>(elem);
        root.inspectDepth(searcher, false);
        return searcher.result;
    }

    @Override
    public boolean remove(Object o) {
        try {
            ElementRemover<T> remover = new ElementRemover<>((T) o);
            root.inspectDepth(remover, false);
            return remover.isRemoved;
        } catch (ClassCastException e) {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public void add(int index, T element) {
        throw new UnsupportedOperationException();
    }

    @Override
    public T set(int index, T element) {
        throw new UnsupportedOperationException();
    }

    @Override
    public T get(int index) {
        throw new UnsupportedOperationException();
    }

    @Override
    public T remove(int index) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(int index, Collection<? extends T> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<T> subList(int fromIndex, int toIndex) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void removeRange(int fromIndex, int toIndex) {
        throw new UnsupportedOperationException();
    }

    static class Entry<T> implements Serializable {
        T element;
        int lineNumber;
        boolean availableToAddLeftChildren = true, availableToAddRightChildren = true;
        Entry<T> parent, leftChild, rightChild;

        public Entry(T element) {
            this.element = element;
        }

        void checkChildren() {
            if (leftChild != null) availableToAddLeftChildren = false;
            if (rightChild != null) availableToAddRightChildren = false;
        }

        boolean isAvailableToAddChildren() {
            return availableToAddLeftChildren || availableToAddRightChildren;
        }

        void inspectDepth(EntryVisitor<Entry<T>> visitor, boolean visitRoot) {
            LinkedList<Entry<T>> entries = new LinkedList<>();
            Command cmd = visitRoot ? visitor.visit(this) : Command.CONTINUE;
            if (this.rightChild != null) entries.offerLast(this.rightChild);
            if (this.leftChild != null) entries.offerLast(this.leftChild);

            Entry<T> entry;
            while (cmd != Command.TERMINATE && (entry = entries.pollLast()) != null) {
                if ((cmd = visitor.visit(entry)) == Command.TERMINATE) return;
                if (entry.rightChild != null) entries.offerLast(entry.rightChild);
                if (entry.leftChild != null) entries.offerLast(entry.leftChild);
            }
        }

        void inspectWidth(EntryVisitor<Entry<T>> visitor, boolean visitRoot) {
            LinkedList<Entry<T>> entries = new LinkedList<>();
            Command cmd = visitRoot ? visitor.visit(this) : Command.CONTINUE;
            if (this.leftChild != null) entries.offerLast(this.leftChild);
            if (this.rightChild != null) entries.offerLast(this.rightChild);

            Entry<T> entry = null;
            while (cmd != Command.TERMINATE && (entry = entries.pollFirst()) != null) {
                if ((cmd = visitor.visit(entry)) == Command.TERMINATE) return;
                if (entry.leftChild != null) entries.offerLast(entry.leftChild);
                if (entry.rightChild != null) entries.offerLast(entry.rightChild);
            }
        }
    }

    private static class ElementAdder<T> implements EntryVisitor<Entry<T>> {
        T target;
        boolean isAdded;

        public ElementAdder(T target) {
            this.target = target;
        }

        @Override
        public Command visit(Entry<T> subject) {
            Command cmd = subject.isAvailableToAddChildren() ? Command.TERMINATE : Command.CONTINUE;
            if (cmd == Command.CONTINUE) return cmd;
            Entry<T> child = new Entry<>(target);
            if (subject.availableToAddLeftChildren) subject.leftChild = child;
            else if (subject.availableToAddRightChildren) subject.rightChild = child;
            child.lineNumber = subject.lineNumber + 1;
            child.parent = subject;
            subject.checkChildren();
            isAdded = true;
            return cmd;
        }
    }

    public static class ForcedElementAdder<T> extends ElementAdder<T> {
        public ForcedElementAdder(T target) {
            super(target);
        }

        @Override
        public Command visit(Entry<T> subject) {
            if (subject.leftChild != null && subject.rightChild != null) return Command.CONTINUE;
            Entry<T> child = new Entry<>(target);
            if (subject.leftChild == null) subject.leftChild = child;
            else subject.rightChild = child;
            child.lineNumber = subject.lineNumber + 1;
            child.parent = subject;
            isAdded = true;
            return Command.TERMINATE;
        }
    }

    private static class ParentSearcher<T> implements EntryVisitor<Entry<T>> {
        private T result;
        private T target;

        public ParentSearcher(T target) {
            this.target = target;
        }

        @Override
        public Command visit(Entry<T> subject) {
            if (subject.element != null && subject.element.equals(target)) {
                result = subject.parent.element;
                return Command.TERMINATE;
            }
            return Command.CONTINUE;
        }
    }

    private static class SizeReviser<T> implements EntryVisitor<Entry<T>> {
        private int size = 0;

        @Override
        public Command visit(Entry<T> subject) {
            size++;
            return Command.CONTINUE;
        }
    }

    private static class ElementRemover<T> implements EntryVisitor<Entry<T>> {
        T target;
        boolean isRemoved;

        public ElementRemover(T target) {
            this.target = target;
        }

        @Override
        public Command visit(Entry<T> subject) {
            Command cmd = target.equals(subject.element) ? Command.TERMINATE : Command.CONTINUE;
            if (cmd == Command.CONTINUE) return cmd;
            if (subject == subject.parent.leftChild) subject.parent.leftChild = null;
            else if (subject == subject.parent.rightChild) subject.parent.rightChild = null;
            isRemoved = true;
            return cmd;
        }
    }
}
