package implementations;

import java.io.Serializable;

/**
 * Represents a node in a binary search tree.
 * 
 * @param <E> The type of element stored in the node, must implement Serializable.
 */
public class BSTreeNode<E> implements Serializable {
    private static final long serialVersionUID = 1L;

    private E element;
    private BSTreeNode<E> left, right;
    
    /**
     * Constructs a new BSTreeNode with the specified element and child nodes.
     * 
     * @param elem  The element to store in the node.
     * @param left  The left child of the node.
     * @param right The right child of the node.
     */
    public BSTreeNode(E elem, BSTreeNode<E> left, BSTreeNode<E> right) {
        this.element = elem;
        this.left = left;
        this.right = right;
    }

    /**
     * Constructs a new BSTreeNode with the specified element.
     * The left and right child nodes are initialized to null.
     * 
     * @param elem The element to store in the node.
     */	
    public BSTreeNode(E elem) {
        this.element = elem;
    }

    /**
     * Retrieves the element stored in the node.
     * 
     * @return The element stored in the node.
     */
    public E getElement() {
        return element;
    }

    /**
     * Retrieves the left child of the node.
     * 
     * @return The left child node, or null if no left child exists.
     */
    public BSTreeNode<E> getLeft() {
        return left;
    }

    /**
     * Retrieves the right child of the node.
     * 
     * @return The right child node, or null if no right child exists.
     */
    public BSTreeNode<E> getRight() {
        return right;
    }

    /**
     * Sets the element stored in the node.
     * 
     * @param element The new element to store in the node.
     */
    public void setElement(E element) {
        this.element = element;
    }

    /**
     * Sets the left child of the node.
     * 
     * @param left The new left child node.
     */
    public void setLeft(BSTreeNode<E> left) {
        this.left = left;
    }

    /**
     * Sets the right child of the node.
     * 
     * @param right The new right child node.
     */
    public void setRight(BSTreeNode<E> right) {
        this.right = right;
    }
}
