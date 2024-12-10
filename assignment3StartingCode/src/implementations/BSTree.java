package implementations;

import java.util.NoSuchElementException;
import java.util.Stack;

import utilities.BSTreeADT;
import utilities.Iterator;

public class BSTree<E extends Comparable<? super E>> implements BSTreeADT<E> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private BSTreeNode<E> root;
	private int size;

	public BSTree() {
		this.root = null;
		size = 0;
	}

	public BSTree(E element) {
		this.root = new BSTreeNode<E>(element, null, null);
	}

	@Override
	public BSTreeNode<E> getRoot() throws NullPointerException {
		if (isEmpty()) {
			throw new NullPointerException("The tree is empty.");
		}
		return root;
	}

	@Override
	public int getHeight() {
		return calculateHeight(root);
	}
	
	private int calculateHeight(BSTreeNode<E> node) {
		if (node == null) {
			return 0;
		}
		int leftHeight = calculateHeight(node.getLeft());
	    int rightHeight = calculateHeight(node.getRight());
	    return 1 + Math.max(leftHeight, rightHeight);
	}

	@Override
	public int size() {
		return size;
	}

	@Override
	public boolean isEmpty() {
		return root == null;
	}

	@Override
	public void clear() {
		root = null;
		size = 0;
	}

	@Override
	public boolean contains(E entry) throws NullPointerException {
	    if (entry == null) {
	        throw new NullPointerException("Unable to search for a null value.");
	    }

	    BSTreeNode<E> current = root;
	    while (current != null) {
	        int compareResult = entry.compareTo(current.getElement());
	        if (compareResult < 0) {
	            current = current.getLeft();
	        } else if (compareResult > 0) {
	            current = current.getRight();
	        } else {
	            return true;
	        }
	    }
	    return false;
	}

	@Override
	public BSTreeNode<E> search(E entry) throws NullPointerException {
		if (entry == null) {
			throw new NullPointerException("Cannot search a null value.");
		}
		
		BSTreeNode<E> current = root;
	    while (current != null) {
	        int comparison = entry.compareTo(current.getElement());
	        if (comparison == 0) {
	            return current;
	        } else if (comparison < 0) {
	            current = current.getLeft();
	        } else {
	            current = current.getRight();
	        }
	    }
	    return null;
	}

	@Override
	public boolean add(E newEntry) throws NullPointerException {
		if (newEntry == null) {
			throw new NullPointerException("Cannot add a null value.");
		}

		if (root == null) {
			root = new BSTreeNode<E>(newEntry);
			size++;
			return true;
		}

		BSTreeNode<E> current = root;
		while (true) {
			int comparison = newEntry.compareTo(current.getElement());

			if (comparison == 0) {
				return false;
			} else if (comparison < 0) {
				if (current.getLeft() == null) {
					current.setLeft(new BSTreeNode<>(newEntry));
					size++;
					return true;
				}
				current = current.getLeft();
			} else {
				if (current.getRight() == null) {
					current.setRight(new BSTreeNode<>(newEntry));
					size++;
					return true;
				}
				current = current.getRight();
			}
		}

	}

	@Override
	public BSTreeNode<E> removeMin() {
		if (root == null) {
			return null;
		}
		
		if (root.getLeft() == null) {
			BSTreeNode<E> minNode = root;
			root = root.getRight();		
			size--;
			return minNode;
		}
		
		BSTreeNode<E> parent = root;
	    BSTreeNode<E> current = root.getLeft();

	    while (current.getLeft() != null) {
	        parent = current;
	        current = current.getLeft();
	    }

	    parent.setLeft(current.getRight());
	    size--;
	    return current;
	}

	@Override
	public BSTreeNode<E> removeMax() {
		if (root == null) {
	        return null;
	    }

        BSTreeNode<E> parent = null;
        BSTreeNode<E> current = root;

        while (current.getRight() != null) {
            parent = current;
            current = current.getRight();
        }

        if (parent == null) {
            root = root.getLeft();
        } else {
            parent.setRight(current.getLeft());
        }

        size--;
        return current;
	}

	@Override
	public Iterator<E> inorderIterator() {
	    return new InorderIterator<>(root);
	}
	
	//Adapted from: geeksforgeeks
	//Source: https://www.geeksforgeeks.org/binary-tree-iterator-for-inorder-traversal/

	public class InorderIterator<E> implements Iterator<E> {
	    private Stack<BSTreeNode<E>> traversal;

	    public InorderIterator(BSTreeNode<E> root) {
	        traversal = new Stack<>();
	        moveLeft(root);
	    }

	    private void moveLeft(BSTreeNode<E> current) {
	        while (current != null) {
	            traversal.push(current);
	            current = current.getLeft();
	        }
	    }

	    @Override
	    public boolean hasNext() {
	        return !traversal.isEmpty();
	    }

	    @Override
	    public E next() {
	        if (!hasNext()) {
	            throw new NoSuchElementException();
	        }

	        BSTreeNode<E> current = traversal.pop();
	        if (current.getRight() != null) {
	            moveLeft(current.getRight());
	        }

	        return current.getElement();
	    }
	}
	
	@Override
	public Iterator<E> preorderIterator() {
		return new PreorderIterator<>(root);
	}

	//Adapted from: geeksforgeeks
	//Source: https://www.geeksforgeeks.org/iterative-preorder-traversal/
	 public class PreorderIterator<E> implements Iterator<E> {

	        private Stack<BSTreeNode<E>> stack;

	        public PreorderIterator(BSTreeNode<E> root) {
	            stack = new Stack<>();
	            if (root != null) {
	                stack.push(root);
	            }
	        }

	        @Override
	        public boolean hasNext() {
	            return !stack.isEmpty();
	        }

	        @Override
	        public E next() {
	            if (!hasNext()) {
	                throw new NoSuchElementException();
	            }

	            BSTreeNode<E> currentNode = stack.pop();
	            if (currentNode.getRight() != null) {
	                stack.push(currentNode.getRight());
	            }
	            if (currentNode.getLeft() != null) {
	                stack.push(currentNode.getLeft());
	            }

	            return currentNode.getElement();
	        }
	    }
	
	@Override
	public Iterator<E> postorderIterator() {
		return new PostorderIterator(root);
	}
	
	//Adapted from: geeksforgeeks
	//Source: https://www.geeksforgeeks.org/iterative-postorder-traversal/

	public class PostorderIterator implements Iterator<E> {
	    private final Stack<E> stack = new Stack<>();

	    public PostorderIterator(BSTreeNode<E> root) {
	        if (root != null) {
	            preparePostorder(root);
	        }
	    }

	    private void preparePostorder(BSTreeNode<E> node) {
	        Stack<BSTreeNode<E>> tempStack = new Stack<>();
	        tempStack.push(node);

	        while (!tempStack.isEmpty()) {
	            BSTreeNode<E> current = tempStack.pop();
	            stack.push(current.getElement());

	            if (current.getLeft() != null) {
	                tempStack.push(current.getLeft());
	            }
	            if (current.getRight() != null) {
	                tempStack.push(current.getRight());
	            }
	        }
	    }

	    @Override
	    public boolean hasNext() {
	        return !stack.isEmpty();
	    }

	    @Override
	    public E next() {
	        if (!hasNext()) {
	            throw new NoSuchElementException();
	        }
	        return stack.pop();
	    }
	}

}
