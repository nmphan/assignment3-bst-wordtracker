package implementations;

import java.io.Serializable;

public class BSTreeNode<E> implements Serializable {
    private static final long serialVersionUID = 1L;

    private E element;
    private BSTreeNode<E> left, right;

    public BSTreeNode(E elem, BSTreeNode<E> left, BSTreeNode<E> right) {
        this.element = elem;
        this.left = left;
        this.right = right;
    }

    public BSTreeNode(E elem) {
        this.element = elem;
    }

    public E getElement() {
        return element;
    }

    public BSTreeNode<E> getLeft() {
        return left;
    }

    public BSTreeNode<E> getRight() {
        return right;
    }

    public void setElement(E element) {
        this.element = element;
    }

    public void setLeft(BSTreeNode<E> left) {
        this.left = left;
    }

    public void setRight(BSTreeNode<E> right) {
        this.right = right;
    }
}
