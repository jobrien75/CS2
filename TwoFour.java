import java.util.Arrays;

public class TwoFourTree {
    private class TwoFourTreeItem {
        int values; // number of values (1 to 3)
        int value1, value2, value3;
        boolean isLeaf;
        
        TwoFourTreeItem parent;
        TwoFourTreeItem leftChild, centerLeftChild, centerChild, centerRightChild, rightChild;

        public TwoFourTreeItem(int value1) {
            this.values = 1;
            this.value1 = value1;
            this.value2 = this.value3 = 0;
            this.isLeaf = true;
        }

        public TwoFourTreeItem(int value1, int value2) {
            this.values = 2;
            if (value1 < value2) {
                this.value1 = value1;
                this.value2 = value2;
            } else {
                this.value1 = value2;
                this.value2 = value1;
            }
            this.value3 = 0;
            this.isLeaf = true;
        }

        public TwoFourTreeItem(int value1, int value2, int value3) {
            this.values = 3;
            int[] arr = {value1, value2, value3};
            Arrays.sort(arr);
            this.value1 = arr[0];
            this.value2 = arr[1];
            this.value3 = arr[2];
            this.isLeaf = true;
        }

        public boolean isTwoNode() { return values == 1; }
        public boolean isThreeNode() { return values == 2; }
        public boolean isFourNode() { return values == 3; }
        public boolean isRoot() { return parent == null; }

        private void printIndents(int indent) {
            for(int i = 0; i < indent; i++) System.out.printf("  ");
        }

        public void printInOrder(int indent) {
            if (!isLeaf) leftChild.printInOrder(indent + 1);
            printIndents(indent);
            System.out.printf("%d\n", value1);
            if (isThreeNode()) {
                if (!isLeaf) centerChild.printInOrder(indent + 1);
                printIndents(indent);
                System.out.printf("%d\n", value2);
            } else if (isFourNode()) {
                if (!isLeaf) centerLeftChild.printInOrder(indent + 1);
                printIndents(indent);
                System.out.printf("%d\n", value2);
                if (!isLeaf) centerRightChild.printInOrder(indent + 1);
                printIndents(indent);
                System.out.printf("%d\n", value3);
            }
            if (!isLeaf) rightChild.printInOrder(indent + 1);
        }

        public int getValueAt(int idx) {
            switch(idx) {
                case 0: return value1;
                case 1: return value2;
                case 2: return value3;
                default: throw new IllegalArgumentException("Invalid value index");
            }
        }

        public void setValueAt(int idx, int v) {
            switch(idx) {
                case 0: value1 = v; break;
                case 1: value2 = v; break;
                case 2: value3 = v; break;
                default: throw new IllegalArgumentException("Invalid value index");
            }
        }

        public TwoFourTreeItem[] getChildren() {
            int count = values + 1;
            TwoFourTreeItem[] c = new TwoFourTreeItem[count];
            if (isTwoNode()) {
                c[0] = leftChild;
                c[1] = rightChild;
            } else if (isThreeNode()) {
                c[0] = leftChild;
                c[1] = centerChild;
                c[2] = rightChild;
            } else {
                c[0] = leftChild;
                c[1] = centerLeftChild;
                c[2] = centerRightChild;
                c[3] = rightChild;
            }
            return c;
        }

        public void setChildren(TwoFourTreeItem[] children) {
            leftChild = centerLeftChild = centerChild = centerRightChild = rightChild = null;
            int count = children.length;
            this.isLeaf = (count == 0);
            if (count == 2) {
                leftChild = children[0];
                rightChild = children[1];
                if (leftChild != null) leftChild.parent = this;
                if (rightChild != null) rightChild.parent = this;
            } else if (count == 3) {
                leftChild = children[0];
                centerChild = children[1];
                rightChild = children[2];
                if (leftChild != null) leftChild.parent = this;
                if (centerChild != null) centerChild.parent = this;
                if (rightChild != null) rightChild.parent = this;
            } else if (count == 4) {
                leftChild = children[0];
                centerLeftChild = children[1];
                centerRightChild = children[2];
                rightChild = children[3];
                if (leftChild != null) leftChild.parent = this;
                if (centerLeftChild != null) centerLeftChild.parent = this;
                if (centerRightChild != null) centerRightChild.parent = this;
                if (rightChild != null) rightChild.parent = this;
            } else if (count == 0) {
                // leaf
            } else {
                throw new IllegalArgumentException("Invalid children count");
            }
        }

        public int findKeyIndex(int key) {
            int idx = 0;
            while (idx < values && key > getValueAt(idx)) idx++;
            return idx;
        }

        public void insertValueAt(int v, int idx) {
            if (values == 1) {
                if (idx == 0) {
                    value2 = value1;
                    value1 = v;
                } else {
                    value2 = v;
                }
                values = 2;
            } else if (values == 2) {
                if (idx == 0) {
                    value3 = value2;
                    value2 = value1;
                    value1 = v;
                } else if (idx == 1) {
                    value3 = value2;
                    value2 = v;
                } else {
                    value3 = v;
                }
                values = 3;
            } else {
                throw new IllegalStateException("Cannot insert into full node");
            }
        }

        public int removeValueAt(int idx) {
            int removed;
            if (values == 1) {
                removed = value1;
                value1 = 0;
                values = 0;
            } else if (values == 2) {
                if (idx == 0) {
                    removed = value1;
                    value1 = value2;
                } else {
                    removed = value2;
                }
                value2 = 0;
                values = 1;
            } else {
                if (idx == 0) {
                    removed = value1;
                    value1 = value2;
                    value2 = value3;
                } else if (idx == 1) {
                    removed = value2;
                    value2 = value3;
                } else {
                    removed = value3;
                }
                value3 = 0;
                values = 2;
            }
            return removed;
        }

        public void insertChildAt(int idx, TwoFourTreeItem child) {
            TwoFourTreeItem[] oldC = getChildren();
            TwoFourTreeItem[] newC = new TwoFourTreeItem[oldC.length + 1];
            for (int i = 0; i < idx; i++) newC[i] = oldC[i];
            newC[idx] = child;
            child.parent = this;
            for (int i = idx; i < oldC.length; i++) newC[i + 1] = oldC[i];
            setChildren(newC);
            this.isLeaf = false;
        }

        public void removeChildAt(int idx) {
            TwoFourTreeItem[] oldC = getChildren();
            TwoFourTreeItem[] newC = new TwoFourTreeItem[oldC.length - 1];
            for (int i = 0, j = 0; i < oldC.length; i++) {
                if (i == idx) continue;
                newC[j++] = oldC[i];
            }
            setChildren(newC);
        }
    }

    private TwoFourTreeItem root;

    public TwoFourTree() {
        root = null;
    }

    public boolean hasValue(int value) {
        TwoFourTreeItem node = root;
        while (node != null) {
            int i = node.findKeyIndex(value);
            if (i < node.values && node.getValueAt(i) == value) {
                return true;
            }
            if (node.isLeaf) return false;
            node = node.getChildren()[i];
        }
        return false;
    }

    public boolean addValue(int value) {
        if (root == null) {
            root = new TwoFourTreeItem(value);
            return true;
        }
        if (hasValue(value)) {
            return false;
        }
        if (root.isFourNode()) {
            splitRoot();
        }
        insertNonFull(root, value);
        return true;
    }

    private void splitRoot() {
        TwoFourTreeItem oldRoot = root;
        int mid = oldRoot.value2;
        TwoFourTreeItem left = new TwoFourTreeItem(oldRoot.value1);
        TwoFourTreeItem right = new TwoFourTreeItem(oldRoot.value3);
        left.isLeaf = oldRoot.isLeaf;
        right.isLeaf = oldRoot.isLeaf;
        if (!oldRoot.isLeaf) {
            TwoFourTreeItem[] children = oldRoot.getChildren();
            left.setChildren(new TwoFourTreeItem[]{children[0], children[1]});
            right.setChildren(new TwoFourTreeItem[]{children[2], children[3]});
        }
        root = new TwoFourTreeItem(mid);
        root.isLeaf = false;
        root.setChildren(new TwoFourTreeItem[]{left, right});
    }

    private void insertNonFull(TwoFourTreeItem node, int value) {
        if (node.isLeaf) {
            int idx = node.findKeyIndex(value);
            node.insertValueAt(value, idx);
        } else {
            int idx = node.findKeyIndex(value);
            TwoFourTreeItem child = node.getChildren()[idx];
            if (child.isFourNode()) {
                splitChild(node, idx);
                if (value > node.getValueAt(idx)) {
                    idx++;
                }
                child = node.getChildren()[idx];
            }
            insertNonFull(child, value);
        }
    }

    private void splitChild(TwoFourTreeItem parent, int idx) {
        TwoFourTreeItem child = parent.getChildren()[idx];
        int mid = child.value2;
        TwoFourTreeItem left = new TwoFourTreeItem(child.value1);
        TwoFourTreeItem right = new TwoFourTreeItem(child.value3);
        left.isLeaf = child.isLeaf;
        right.isLeaf = child.isLeaf;
        if (!child.isLeaf) {
            TwoFourTreeItem[] c = child.getChildren();
            left.setChildren(new TwoFourTreeItem[]{c[0], c[1]});
            right.setChildren(new TwoFourTreeItem[]{c[2], c[3]});
        }
        parent.removeChildAt(idx);
        parent.insertChildAt(idx, left);
        parent.insertChildAt(idx + 1, right);
        parent.insertValueAt(mid, idx);
    }

    public boolean deleteValue(int value) {
        if (!hasValue(value) || root == null) {
            return false;
        }
        delete(root, value);
        if (root.values == 0) {
            if (!root.isLeaf) {
                root = root.leftChild;
                root.parent = null;
            } else {
                root = null;
            }
        }
        return true;
    }

    private void delete(TwoFourTreeItem node, int key) {
        int idx = node.findKeyIndex(key);
        if (idx < node.values && node.getValueAt(idx) == key) {
            if (node.isLeaf) {
                node.removeValueAt(idx);
            } else {
                TwoFourTreeItem pred = node.getChildren()[idx];
                if (pred.values >= 2) {
                    int predKey = getPredecessor(pred);
                    node.setValueAt(idx, predKey);
                    delete(pred, predKey);
                } else {
                    TwoFourTreeItem succ = node.getChildren()[idx + 1];
                    if (succ.values >= 2) {
                        int succKey = getSuccessor(succ);
                        node.setValueAt(idx, succKey);
                        delete(succ, succKey);
                    } else {
                        mergeChildren(node, idx);
                        delete(pred, key);
                    }
                }
            }
        } else {
            if (node.isLeaf) {
                return;
            }
            TwoFourTreeItem child = node.getChildren()[idx];
            if (child.values == 1) {
                if (idx > 0 && node.getChildren()[idx - 1].values >= 2) {
                    borrowFromLeft(node, idx);
                } else if (idx < node.values && node.getChildren()[idx + 1].values >= 2) {
                    borrowFromRight(node, idx);
                } else {
                    if (idx < node.values) {
                        mergeChildren(node, idx);
                    } else {
                        mergeChildren(node, idx - 1);
                        idx--;
                    }
                }
                child = node.getChildren()[idx];
            }
            delete(child, key);
        }
    }

    private int getPredecessor(TwoFourTreeItem node) {
        TwoFourTreeItem cur = node;
        while (!cur.isLeaf) {
            cur = cur.getChildren()[cur.values];
        }
        return cur.getValueAt(cur.values - 1);
    }

    private int getSuccessor(TwoFourTreeItem node) {
        TwoFourTreeItem cur = node;
        while (!cur.isLeaf) {
            cur = cur.getChildren()[0];
        }
        return cur.getValueAt(0);
    }

    private void mergeChildren(TwoFourTreeItem parent, int idx) {
        TwoFourTreeItem left = parent.getChildren()[idx];
        TwoFourTreeItem right = parent.getChildren()[idx + 1];
        int sep = parent.getValueAt(idx);
        left.insertValueAt(sep, left.values);
        for (int i = 0; i < right.values; i++) {
            left.insertValueAt(right.getValueAt(i), left.values);
        }
        if (!left.isLeaf) {
            TwoFourTreeItem[] lChildren = left.getChildren();
            TwoFourTreeItem[] rChildren = right.getChildren();
            TwoFourTreeItem[] nc = new TwoFourTreeItem[lChildren.length + rChildren.length];
            System.arraycopy(lChildren, 0, nc, 0, lChildren.length);
            System.arraycopy(rChildren, 0, nc, lChildren.length, rChildren.length);
            left.setChildren(nc);
        }
        parent.removeValueAt(idx);
        parent.removeChildAt(idx + 1);
    }

    private void borrowFromLeft(TwoFourTreeItem parent, int idx) {
        TwoFourTreeItem child = parent.getChildren()[idx];
        TwoFourTreeItem left = parent.getChildren()[idx - 1];
        int sep = parent.getValueAt(idx - 1);
        int borrow = left.getValueAt(left.values - 1);
        parent.setValueAt(idx - 1, borrow);
        left.removeValueAt(left.values - 1);
        child.insertValueAt(sep, 0);
        if (!left.isLeaf) {
            TwoFourTreeItem[] lChildren = left.getChildren();
            TwoFourTreeItem bc = lChildren[lChildren.length - 1];
            left.removeChildAt(lChildren.length - 1);
            child.insertChildAt(0, bc);
        }
    }

    private void borrowFromRight(TwoFourTreeItem parent, int idx) {
        TwoFourTreeItem child = parent.getChildren()[idx];
        TwoFourTreeItem right = parent.getChildren()[idx + 1];
        int sep = parent.getValueAt(idx);
        int borrow = right.getValueAt(0);
        parent.setValueAt(idx, borrow);
        right.removeValueAt(0);
        child.insertValueAt(sep, child.values);
        if (!right.isLeaf) {
            TwoFourTreeItem bc = right.getChildren()[0];
            right.removeChildAt(0);
            child.insertChildAt(child.getChildren().length, bc);
        }
    }

    public void printInOrder() {
        if (root != null) root.printInOrder(0);
    }
}