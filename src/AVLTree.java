import java.util.*;

/**
 * Created by Dimitar on 2.3.14.
 */
public class AVLTree<T extends Comparable<T>, V> {
    private AVLNode<T, V> root;

    public AVLTree(){
        root = null;
    }

    public void insert(T key, V value){
        root = insertR(key, root, value);
    }

    public void delete(T key){
        root = deleteR(key, root);
    }

    public void inorder(){
        inorderR(root);
    }

    public void nodesInRange(T from, T to){
        nodesInRangeR(from, to, root);
    }

    public int countNodesInRange(T from, T to){
        return countNodesInRangeR(from, to, root);
    }

    public ArrayList<V> nodesInRangeArray(T from, T to){

        return nodesInRangeArrayR(from, to, root, new ArrayList<V>());
    }

    public T findMaxInRange(T from, T to){
        return findMaxInRangeR(to, root);
    }

    public T findMinInRange(T from, T to){
        return findMinInRangeR(from, root);
    }

    private T findMinInRangeR(T from, AVLNode<T, V> node){
        if (node != null){
            int cmpFrom = node.key.compareTo(from);

            if (cmpFrom > 0){
                T min = findMinInRangeR(from, node.leftLink);
                if (min == null){
                    return node.key;
                }
                return min;
            }
            if (cmpFrom == 0){
                return node.key;
            }
            if (cmpFrom < 0){
                return findMinInRangeR(from, node.rightLink);
            }
        }

        return null;
    }

    private T findMaxInRangeR(T to, AVLNode<T, V> node){
        if (node != null){
            int cmpTo = node.key.compareTo(to);

            if (cmpTo < 0){
                T max = findMaxInRangeR(to, node.rightLink);
                if (max == null){
                    return node.key;
                }
                return max;
            }
            if (cmpTo == 0){
                return node.key;
            }
            if (cmpTo > 0){
                return findMaxInRangeR(to, node.leftLink);
            }
        }

        return null;
    }

    private ArrayList<V> nodesInRangeArrayR(T from, T to, AVLNode<T, V> node, ArrayList<V> list){
        if (node != null){
            int cmpFrom = node.key.compareTo(from);
            int cmpTo = node.key.compareTo(to);

            if (cmpFrom > 0){
                list = nodesInRangeArrayR(from, to, node.leftLink, list);
            }
            if (cmpFrom >= 0 && cmpTo <= 0){
                list.add(node.value);
            }
            if (cmpTo < 0){
                list = nodesInRangeArrayR(from, to, node.rightLink, list);
            }
        }

        return list;
    }

    private int countNodesInRangeR(T from, T to, AVLNode<T, V> node){
        if (node == null) return 0;
        //Compare key in node with from, 1 if key is bigger than from, 0 if is equal, -1 if key is smaller than from
        int cmpFrom = node.key.compareTo(from);
        //Compare key in node with to, 1 if key is bigger than to, 0 if is equal, -1 if key is smaller than to
        int cmpTo = node.key.compareTo(to);

        if (cmpFrom > 0 && cmpTo < 0){
            return 1 + countBigger(from, node.leftLink) + countSmaller(to, node.rightLink);
        } else if (cmpFrom == 0){
            return 1 + countSmaller(to, node.rightLink);
        } else if (cmpTo == 0){
            return 1 + countBigger(from, node.leftLink);
        } else if (cmpFrom < 0){
            return countNodesInRangeR(from, to, node.rightLink);
        } else if (cmpTo > 0){
            return countNodesInRangeR(from, to, node.leftLink);
        }

        return 0;
    }

    private int countSmaller(T to, AVLNode<T, V> node){
        if (node == null) return 0;
        //Compare key in node with to
        int cmp = node.key.compareTo(to);
        //If key in node is smaller than to
        if (cmp < 0){
            int count = 1;
            if (node.leftLink != null) count += node.leftLink.count;
            return count += countSmaller(to, node.rightLink);
        } else if (cmp == 0){ //If key in node is equal to to
            int count = 1;
            if (node.leftLink != null) count += node.leftLink.count;
            return count;
        } else if (cmp > 0){ //If key in node is bigger that to
            return countSmaller(to, node.leftLink);
        }
        //Should never happen
        return 0;
    }
    private int countBigger(T from, AVLNode<T, V> node){
        if (node == null) return 0;
        //Compare key in node with from
        int cmp = node.key.compareTo(from);
        //If key in node is bigger than from
        if (cmp > 0){
            int count = 1;
            if (node.rightLink != null) count += node.rightLink.count;
            return count + countBigger(from, node.leftLink);
        } else if (cmp == 0){ //If key in node is equal to from
            int count = 1;
            if (node.rightLink != null) count += node.rightLink.count;
            return count;
        } else if (cmp < 0){ //If key in node is smaller than from
            return countBigger(from, node.rightLink);
        }
        //Should never happen
        return 0;
    }

    private void nodesInRangeR(T from, T to, AVLNode<T, V> node){
        if (node != null){
            //Compare the key in node with from
            int cmpFrom = node.key.compareTo(from);
            //Compare the key in node with to
            int cmpTo = node.key.compareTo(to);
            //If the key in node is bigger than from
            if(cmpFrom > 0){
                nodesInRangeR(from, to, node.leftLink);
            }
            //If the key in node is equalOrBigger than from, and equalOrSmaller than from
            if(cmpFrom >= 0 && cmpTo <= 0){
                System.out.println(node.key);
            }
            //If the key in node is smaller that to
            if (cmpTo < 0){
                nodesInRangeR(from, to, node.rightLink);
            }
        }
    }

    private AVLNode<T, V> deleteR(T value, AVLNode<T, V> node){
        if (node == null) return node;

        int cmp = value.compareTo(node.key);

        if (cmp < 0) node.leftLink = deleteR(value, node.leftLink);
        if (cmp > 0) node.rightLink = deleteR(value, node.rightLink);

        if(cmp == 0){
            //No child
            if (node.leftLink == null && node.rightLink == null){
                return null;
            }
            //Left child
            if (node.leftLink != null && node.rightLink == null){
                return node.leftLink;
            }
            //Right child
            if (node.rightLink != null && node.leftLink == null){
                return node.rightLink;
            }
            //Both child
            AVLNode<T, V> successor = findMin(node.rightLink);
            node.key = successor.key;
            node.value = successor.value;
            node.rightLink = deleteR(node.key, node.rightLink);
        }

        node.height = max(height(node.leftLink), height(node.rightLink)) + 1;
        node.update();

        //Check if balanceFactor is disturbed
        int balanceFactor = height(node.leftLink) - height(node.rightLink);
        //Unbalance in left subtree
        if(balanceFactor >= 2){
            //balanceFactor of leftLink
            int balanceFactorOfLeftLink = height(node.leftLink.leftLink) - height(node.leftLink.rightLink);
            //Left-Right case
            if(balanceFactorOfLeftLink < 0){
                node.leftLink = rotateLeft(node.leftLink);
            }
            //Left-Left case
            node = rotateRight(node);
        }
        //Unbalance in right subtree
        if (balanceFactor <= -2){
            //balanceFactor of rightLink
            int balanceFactorRightLink = height(node.rightLink.leftLink) - height(node.rightLink.rightLink);
            //Right-Left case
            if(balanceFactorRightLink > 0){
                node.rightLink = rotateRight(node.rightLink);
            }
            //Right-Right case
            node = rotateLeft(node);
        }
        //Update height
        node.height = max(height(node.leftLink), height(node.rightLink)) + 1;
        //Update count of children
        node.update();

        return node;
    }

    private AVLNode<T, V> insertR(T key, AVLNode<T, V> node, V value){
        if (node == null) return new AVLNode<T, V>(key, value);

        int cmp = key.compareTo(node.key);
        //Insert into left subtree
        if (cmp < 0) node.leftLink = insertR(key, node.leftLink, value);
        //Insert into right subtree
        if (cmp > 0) node.rightLink = insertR(key, node.rightLink, value);
        //Calculate balance factor
        int balanceFactor = height(node.leftLink) - height(node.rightLink);
        //If balance factor is not valid, <=-2, >=2
        if (balanceFactor >= 2){
            //Left-Right case
            if(key.compareTo(node.leftLink.key) > 0){
                node.leftLink = rotateLeft(node.leftLink);
            }
            //Left-Left case
            node = rotateRight(node);
        }
        if (balanceFactor <= -2){
            //Right-Left case
            if(key.compareTo(node.rightLink.key) < 0){
                node.rightLink = rotateRight(node.rightLink);
            }
            //Right-Right case
            node = rotateLeft(node);
        }
        //Update height
        node.height = max(height(node.leftLink), height(node.rightLink)) + 1;
        //Update count of children
        node.update();
        return node;
    }

    private void inorderR(AVLNode<T, V> node){
        if (node != null){
            inorderR(node.leftLink);

            T left = null;
            T right = null;
            if (node.leftLink != null) left = node.leftLink.key;
            if (node.rightLink != null) right = node.rightLink.key;
            System.out.println(node.key + " " + node.value + " " + left + " " + right + " " + node.height + " " + node.count);

            inorderR(node.rightLink);
        }
    }

    private AVLNode<T, V> findMin(AVLNode<T, V> node){
        if (node == null) return node;
        if (node.leftLink == null) return node;
        return findMin(node.leftLink);
    }

    private AVLNode<T, V> findMax(AVLNode<T, V> node){
        if (node == null) return node;
        if (node.rightLink == null) return node;
        return findMax(node.rightLink);
    }


    private int max(int a, int b){
        return a > b ? a : b;
    }

    private AVLNode<T, V> rotateRight(AVLNode<T, V> node){
        AVLNode<T, V> temp = node.leftLink;
        node.leftLink = temp.rightLink;
        temp.rightLink = node;
        node.height = max(height(node.leftLink), height(node.rightLink)) + 1;
        temp.height = max(height(temp.leftLink), height(temp.rightLink)) + 1;

        node.update();
        temp.update();

        return temp;
    }

    private AVLNode<T, V> rotateLeft(AVLNode<T, V> node){
        AVLNode<T, V> temp = node.rightLink;
        node.rightLink = temp.leftLink;
        temp.leftLink = node;
        node.height = max(height(node.leftLink), height(node.rightLink)) + 1;
        temp.height = max(height(temp.leftLink), height(temp.rightLink)) + 1;

        node.update();
        temp.update();

        return temp;
    }

    public int height(AVLNode<T, V> node){
        if (node == null) return -1;
        return node.height;
    }

    private class AVLNode<T extends Comparable<T>, V>{
        private T key;
        private V value;
        private int height;
        private int count;
        private AVLNode<T, V> leftLink;
        private AVLNode<T, V> rightLink;

        public AVLNode(T key, V value){
            this.key = key;
            this.value = value;
            height = 0;
            leftLink = null;
            rightLink = null;
            count = 1;
        }

        public void update(){
            count = 1;
            if(leftLink != null) count += leftLink.count;
            if(rightLink != null) count += rightLink.count;
        }
    }

    public static void main(String [] args){
        AVLTree<Integer, Integer> tree = new AVLTree<Integer, Integer>();
        Random random = new Random(System.currentTimeMillis());

        for (int i = 0; i < 10; i++){
            tree.insert(random.nextInt(20), random.nextInt(20) + 20);
        }

        tree.inorder();

        int from = 3;
        int to = 12;

        ArrayList<Integer> list = tree.nodesInRangeArray(from, to);

        for (int i = 0; i<list.size(); i++){
            System.out.print(list.get(i));
            System.out.print(" ");
        }
        System.out.println();

        tree.nodesInRange(from, to);

        System.out.println(tree.countNodesInRange(from, to));

        System.out.println(tree.findMaxInRange(from, to));
        System.out.println(tree.findMinInRange(from, to));
    }
}
