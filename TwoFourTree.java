public class TwoFourTree {
    private class TwoFourTreeItem {
        int values = 1; // number of values in node: 1, 2, or 3
        int value1 = 0; // always used
        int value2 = 0; // used iff values >= 2
        int value3 = 0; // used iff values == 3
        boolean isLeaf = true;

        TwoFourTreeItem parent = null;
        TwoFourTreeItem leftChild = null;
        TwoFourTreeItem centerChild = null;
        TwoFourTreeItem rightChild = null;
        TwoFourTreeItem centerLeftChild = null;
        TwoFourTreeItem centerRightChild = null;

        public boolean isTwoNode() {
            return values == 1;
        }
        public boolean isThreeNode() {
            return values == 2;
        }
        public boolean isFourNode() {
            return values == 3;
        }
        public boolean isRoot() {
            return parent == null;
        }

        public TwoFourTreeItem(int value1) {
            this.value1 = value1;
            this.values = 1;
            this.isLeaf = true;
        }
        public TwoFourTreeItem(int value1, int value2) {
            this.value1 = Math.min(value1, value2);
            this.value2 = Math.max(value1, value2);
            this.values = 2;
            this.isLeaf = true;
        }
        public TwoFourTreeItem(int value1, int value2, int value3) {
            int[] arr = {value1, value2, value3};
            java.util.Arrays.sort(arr);
            this.value1 = arr[0];
            this.value2 = arr[1];
            this.value3 = arr[2];
            this.values = 3;
            this.isLeaf = true;
        }

        private void printIndents(int indent) {
            for(int i = 0; i < indent; i++) System.out.printf("  ");
        }

        public void printInOrder(int indent) {
            if(!isLeaf) leftChild.printInOrder(indent + 1);
            printIndents(indent);
            System.out.printf("%d\n", value1);
            if(isThreeNode()) {
                if(!isLeaf) centerChild.printInOrder(indent + 1);
                printIndents(indent);
                System.out.printf("%d\n", value2);
            } else if(isFourNode()) {
                if(!isLeaf) centerLeftChild.printInOrder(indent + 1);
                printIndents(indent);
                System.out.printf("%d\n", value2);
                if(!isLeaf) centerRightChild.printInOrder(indent + 1);
                printIndents(indent);
                System.out.printf("%d\n", value3);
            }
            if(!isLeaf) rightChild.printInOrder(indent + 1);
        }
    }

    TwoFourTreeItem root = null;

    public TwoFourTree() { }

    // Public API

    public boolean addValue(int value) {
        if (root == null) {
            root = new TwoFourTreeItem(value);
            return true;
        }

        // If root is full, split it first
        if (root.isFourNode()) {
            splitRoot();
        }

        return insertNonFull(root, value);
    }

    public boolean hasValue(int value) {
        TwoFourTreeItem node = root;
        while (node != null) {
            if (value == node.value1) return true;
            if (node.isThreeNode() && value == node.value2) return true;
            if (node.isFourNode() && value == node.value3) return true;

            if (value < node.value1) {
                node = node.leftChild;
            } else if (node.isTwoNode() || (node.isThreeNode() && value < node.value2)) {
                node = node.isTwoNode() ? node.rightChild : node.centerChild;
            } else if (node.isThreeNode() || (node.isFourNode() && value < node.value2)) {
                if (node.isThreeNode()) {
                    node = node.rightChild;
                } else {
                    node = node.centerLeftChild;
                }
            } else if (node.isFourNode() && value < node.value3) {
                node = node.centerRightChild;
            } else {
                node = node.rightChild;
            }
        }
        return false;
    }

    public boolean deleteValue(int value) {
        if (root == null) return false;
        if (!deleteHelper(root, value)) return false;
        if (root.values == 0 && !root.isLeaf) {
            // root shrunk, move root down
            if (root.leftChild != null) {
                root = root.leftChild;
                root.parent = null;
            }
        }
        return true;
    }

    public void printInOrder() {
        if (root != null) root.printInOrder(0);
    }

    // Internal helpers

    private void splitRoot() {
        // Assumes root is a 4-node
        TwoFourTreeItem oldRoot = root;
        TwoFourTreeItem left = new TwoFourTreeItem(oldRoot.value1);
        TwoFourTreeItem right = new TwoFourTreeItem(oldRoot.value3);
        left.isLeaf = oldRoot.isLeaf;
        right.isLeaf = oldRoot.isLeaf;

        // Move children if not leaf
        if (!oldRoot.isLeaf) {
            left.leftChild = oldRoot.leftChild;
            left.rightChild = oldRoot.centerLeftChild;
            right.leftChild = oldRoot.centerRightChild;
            right.rightChild = oldRoot.rightChild;
            if (left.leftChild != null) left.leftChild.parent = left;
            if (left.rightChild != null) left.rightChild.parent = left;
            if (right.leftChild != null) right.leftChild.parent = right;
            if (right.rightChild != null) right.rightChild.parent = right;
            left.isLeaf = left.leftChild == null && left.rightChild == null;
            right.isLeaf = right.leftChild == null && right.rightChild == null;
        }

        root = new TwoFourTreeItem(oldRoot.value2);
        root.isLeaf = false;
        root.leftChild = left;
        root.rightChild = right;
        left.parent = root;
        right.parent = root;
    }

    // Insert into non-full node
    private boolean insertNonFull(TwoFourTreeItem node, int value) {
        if (node.isLeaf) {
            return addValueToNode(node, value, null, null);
        }

        // Find child to descend
        TwoFourTreeItem next;
        if (value < node.value1) {
            next = node.leftChild;
        } else if (node.isTwoNode() || (node.isThreeNode() && value < node.value2)) {
            next = node.isTwoNode() ? node.rightChild : node.centerChild;
        } else if (node.isThreeNode() || (node.isFourNode() && value < node.value2)) {
            if (node.isThreeNode()) {
                next = node.rightChild;
            } else {
                next = node.centerLeftChild;
            }
        } else if (node.isFourNode() && value < node.value3) {
            next = node.centerRightChild;
        } else {
            next = node.rightChild;
        }

        // If the child is a 4-node, split it before descending
        if (next != null && next.isFourNode()) {
            splitChild(node, next);
            // After split, decide which child to descend into
            if (value < node.value1) {
                next = node.leftChild;
            } else if (node.isTwoNode() || (node.isThreeNode() && value < node.value2)) {
                next = node.isTwoNode() ? node.rightChild : node.centerChild;
            } else if (node.isThreeNode() || (node.isFourNode() && value < node.value2)) {
                if (node.isThreeNode()) {
                    next = node.rightChild;
                } else {
                    next = node.centerLeftChild;
                }
            } else if (node.isFourNode() && value < node.value3) {
                next = node.centerRightChild;
            } else {
                next = node.rightChild;
            }
        }
        return insertNonFull(next, value);
    }

    // Add a value to a node with < 3 values
    private boolean addValueToNode(TwoFourTreeItem node, int value, TwoFourTreeItem left, TwoFourTreeItem right) {
        // No duplicates
        if ((node.values >= 1 && node.value1 == value) ||
            (node.values >= 2 && node.value2 == value) ||
            (node.values == 3 && node.value3 == value)) {
            return false;
        }
        if (node.values == 1) {
            if (value < node.value1) {
                node.value2 = node.value1;
                node.value1 = value;
                // For internal nodes, shift children
                if (!node.isLeaf) {
                    node.centerChild = node.rightChild;
                    node.rightChild = right;
                    node.leftChild = left;
                    if (left != null) left.parent = node;
                    if (right != null) right.parent = node;
                }
            } else {
                node.value2 = value;
                if (!node.isLeaf) {
                    node.centerChild = left;
                    node.rightChild = right;
                    if (left != null) left.parent = node;
                    if (right != null) right.parent = node;
                }
            }
            node.values = 2;
            return true;
        } else if (node.values == 2) {
            int[] arr = new int[]{node.value1, node.value2, value};
            java.util.Arrays.sort(arr);
            node.value1 = arr[0];
            node.value2 = arr[1];
            node.value3 = arr[2];
            node.values = 3;
            // For internal nodes, must handle child pointers
            // This only happens during split, so children are handled elsewhere
            return true;
        }
        // Should never try to add to a full node
        return false;
    }

    private void splitChild(TwoFourTreeItem parent, TwoFourTreeItem node) {
        // node is a 4-node
        TwoFourTreeItem left = new TwoFourTreeItem(node.value1);
        TwoFourTreeItem right = new TwoFourTreeItem(node.value3);
        left.isLeaf = node.isLeaf;
        right.isLeaf = node.isLeaf;

        if (!node.isLeaf) {
            left.leftChild = node.leftChild;
            left.rightChild = node.centerLeftChild;
            right.leftChild = node.centerRightChild;
            right.rightChild = node.rightChild;
            if (left.leftChild != null) left.leftChild.parent = left;
            if (left.rightChild != null) left.rightChild.parent = left;
            if (right.leftChild != null) right.leftChild.parent = right;
            if (right.rightChild != null) right.rightChild.parent = right;
            left.isLeaf = left.leftChild == null && left.rightChild == null;
            right.isLeaf = right.leftChild == null && right.rightChild == null;
        }

        // Insert node.value2 into parent, and replace node with left/right
        if (parent.leftChild == node) {
            // leftmost
            addValueToNode(parent, node.value2, left, right);
            // fix children
            if (parent.values == 2) {
                parent.centerChild = parent.rightChild;
                parent.rightChild = right;
            } else if (parent.values == 3) {
                parent.centerRightChild = parent.rightChild;
                parent.centerLeftChild = right;
                parent.rightChild = parent.centerRightChild;
            }
            parent.leftChild = left;
        } else if (parent.rightChild == node) {
            addValueToNode(parent, node.value2, left, right);
            if (parent.values == 2) {
                parent.centerChild = left;
            } else if (parent.values == 3) {
                parent.centerRightChild = left;
            }
            parent.rightChild = right;
        } else if (parent.centerChild == node) {
            addValueToNode(parent, node.value2, left, right);
            parent.centerChild = left;
            if (parent.values == 3) {
                parent.centerLeftChild = right;
            }
        } else if (parent.centerLeftChild == node) {
            addValueToNode(parent, node.value2, left, right);
            parent.centerLeftChild = left;
            parent.centerRightChild = right;
        }

        left.parent = parent;
        right.parent = parent;
    }

    // ----------- Deletion logic -----------
    // Main helper for deletion
    private boolean deleteHelper(TwoFourTreeItem node, int value) {
        if (node == null) return false;
        int idx = -1;
        if (value == node.value1) idx = 0;
        else if (node.values >= 2 && value == node.value2) idx = 1;
        else if (node.values == 3 && value == node.value3) idx = 2;

        if (idx != -1) {
            if (node.isLeaf) {
                removeFromLeaf(node, idx);
            } else {
                // Replace with predecessor or successor
                if (idx == 0) {
                    int pred = getPredecessor(node, 0);
                    node.value1 = pred;
                    deleteHelper(getPredecessorNode(node, 0), pred);
                } else if (idx == 1) {
                    int pred = getPredecessor(node, 1);
                    node.value2 = pred;
                    deleteHelper(getPredecessorNode(node, 1), pred);
                } else if (idx == 2) {
                    int pred = getPredecessor(node, 2);
                    node.value3 = pred;
                    deleteHelper(getPredecessorNode(node, 2), pred);
                }
            }
            return true;
        } else {
            // Descend to child
            TwoFourTreeItem child = getChildForValue(node, value);
            if (child == null) return false;
            // If child is 2-node, fix before descending
            if (child.values == 1) {
                fixChild(node, child);
            }
            return deleteHelper(child, value);
        }
    }

    private void removeFromLeaf(TwoFourTreeItem node, int idx) {
        if (node.values == 1) {
            // Remove the node entirely
            node.values = 0; // Will be pruned by parent
        } else if (node.values == 2) {
            if (idx == 0) {
                node.value1 = node.value2;
            }
            node.values = 1;
        } else if (node.values == 3) {
            if (idx == 0) {
                node.value1 = node.value2;
                node.value2 = node.value3;
            } else if (idx == 1) {
                node.value2 = node.value3;
            }
            node.values = 2;
        }
    }

    // Fix child so that it's not a 2-node before descending for deletion
    private void fixChild(TwoFourTreeItem parent, TwoFourTreeItem child) {
        // Try to borrow from siblings, otherwise merge
        TwoFourTreeItem leftSibling = getLeftSibling(parent, child);
        TwoFourTreeItem rightSibling = getRightSibling(parent, child);

        if (leftSibling != null && leftSibling.values >= 2) {
            // Borrow from left
            if (child == parent.leftChild) {
                child.value2 = child.value1;
                child.value1 = parent.value1;
                child.values = 2;
                parent.value1 = leftSibling.values == 2 ? leftSibling.value2 : leftSibling.value3;
                leftSibling.values--;
            }
        } else if (rightSibling != null && rightSibling.values >= 2) {
            // Borrow from right
            if (child == parent.rightChild) {
                child.value2 = rightSibling.value1;
                child.values = 2;
                rightSibling.value1 = rightSibling.values == 2 ? rightSibling.value2 : rightSibling.value3;
                rightSibling.values--;
            }
        } else {
            // Merge with sibling
            if (leftSibling != null) {
                // Merge leftSibling + parent value + child
                mergeNodes(parent, leftSibling, child);
            } else if (rightSibling != null) {
                mergeNodes(parent, child, rightSibling);
            }
        }
    }

    // Helper: get the child pointer to descend for a value
    private TwoFourTreeItem getChildForValue(TwoFourTreeItem node, int value) {
        if (value < node.value1) return node.leftChild;
        if (node.values == 1) return node.rightChild;
        if (value < node.value2) return node.centerChild;
        if (node.values == 2) return node.rightChild;
        if (value < node.value3) return node.centerLeftChild;
        if (value < node.value3) return node.centerRightChild;
        return node.rightChild;
    }

    // Get predecessor value for (idx)th value in node
    private int getPredecessor(TwoFourTreeItem node, int idx) {
        TwoFourTreeItem cur = getLeftChildForIdx(node, idx);
        while (!cur.isLeaf) {
            cur = cur.rightChild;
        }
        if (cur.values == 3) return cur.value3;
        else if (cur.values == 2) return cur.value2;
        else return cur.value1;
    }

    // Get the node containing the predecessor
    private TwoFourTreeItem getPredecessorNode(TwoFourTreeItem node, int idx) {
        TwoFourTreeItem cur = getLeftChildForIdx(node, idx);
        while (!cur.isLeaf) {
            cur = cur.rightChild;
        }
        return cur;
    }

    private TwoFourTreeItem getLeftChildForIdx(TwoFourTreeItem node, int idx) {
        if (idx == 0) return node.leftChild;
        if (idx == 1) return node.centerChild;
        if (idx == 2) return node.centerLeftChild;
        return null;
    }

    // Get left sibling of child
    private TwoFourTreeItem getLeftSibling(TwoFourTreeItem parent, TwoFourTreeItem child) {
        if (parent.leftChild == child) return null;
        if (parent.centerChild == child) return parent.leftChild;
        if (parent.rightChild == child) {
            if (parent.values == 2) return parent.centerChild;
            if (parent.values == 3) return parent.centerRightChild;
        }
        if (parent.centerLeftChild == child) return parent.centerChild;
        if (parent.centerRightChild == child) return parent.centerLeftChild;
        return null;
    }

    // Get right sibling of child
    private TwoFourTreeItem getRightSibling(TwoFourTreeItem parent, TwoFourTreeItem child) {
        if (parent.rightChild == child) return null;
        if (parent.leftChild == child) return parent.centerChild;
        if (parent.centerChild == child) {
            if (parent.values == 2) return parent.rightChild;
            if (parent.values == 3) return parent.centerLeftChild;
        }
        if (parent.centerLeftChild == child) return parent.centerRightChild;
        if (parent.centerRightChild == child) return parent.rightChild;
        return null;
    }

    // Merge two children and the parent value between them
    private void mergeNodes(TwoFourTreeItem parent, TwoFourTreeItem left, TwoFourTreeItem right) {
        // Merge left + parent's separator + right
        if (left.values == 1 && right.values == 1) {
            left.value2 = parent.value1;
            left.value3 = right.value1;
            left.values = 3;
            left.rightChild = right.rightChild;
            left.isLeaf = left.leftChild == null && left.centerChild == null &&
                          left.centerLeftChild == null && left.centerRightChild == null && left.rightChild == null;
            // Remove right from parent
            if (parent.leftChild == left && parent.centerChild == right) {
                parent.leftChild = left;
                parent.centerChild = null;
                parent.values = 1;
            } else if (parent.centerChild == left && parent.rightChild == right) {
                parent.centerChild = left;
                parent.rightChild = null;
                parent.values = 1;
            }
        }
        // Parent may shrink
    }
}