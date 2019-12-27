public class RedBlackTree<K extends Comparable<K>, V> {
	private static final boolean RED = true;
	private static final boolean BLACK = false;

	private Entry root; // корень дерева
	private Entry nil; // заглушка

	public RedBlackTree() {
		this.nil = new Entry();
		this.root = nil;
	}

	/*
	** Вложенный класс реализующий узел дерева
	 */

	private class Entry {
		private K key; // ключ по которому происходит выбор места
		private V value; // значение хранящееся в узле
		private Entry parent;
		private Entry left;
		private Entry right;
		private boolean color;

		/*
		** Конструктор инициализирующий заглушку
		 */

		public Entry() {
			this.key = null;
			this.value = null;
			this.parent =  nil;
			this.left = nil;
			this.right = nil;
			this.color = BLACK;
		}

		/*
		 ** Конструктор инициализирующий узел
		 */

		public Entry(K key, V value, boolean color) {
			this.key = key;
			this.value = value;
			this.parent =  nil;
			this.left = nil;
			this.right = nil;
			this.color = color;
		}

		/*
		** Геттеры и Сеттеры
		 */

		public K getKey() {
			return key;
		}

		public V getValue() {
			return value;
		}

		public void setValue(V value) {
			this.value = value;
		}

		public Entry getParent() {
			return parent;
		}

		public void setParent(Entry parent) {
			this.parent = parent;
		}

		public Entry getLeft() {
			return left;
		}

		public void setLeft(Entry left) {
			this.left = left;
		}

		public Entry getRight() {
			return right;
		}

		public void setRight(Entry right) {
			this.right = right;
		}

		public boolean getColor() {
			return color;
		}

		public void setColor(boolean color) {
			this.color = color;
		}

		/*
		** Возвращает деда или заглушку, если его нет
		 */

		private Entry getGrandFather() {
			if (this.parent != null && this.parent != nil)
				return parent.parent;
			return nil;
		}

		/*
		** Возвращает дядю или заглушку, если его нет
		 */

		private Entry getUncle() {
			Entry grandFather = getGrandFather();

			if (grandFather != nil) {
				if (grandFather.left == parent)
					return grandFather.right;
				else
					return grandFather.left;
			}
			return nil;
		}

		public boolean isRight() {
			return this.getParent().getRight() == this;
		}

		public boolean isLeft() {
			return this.getParent().getLeft() == this;
		}

		/*
		** Печатает информацию об узле
		 */

		public void printNode() {
			String color = "B_";
			if (this != nil) {
				if (this.color == RED)
					color = "R_";
				System.out.print(color + this.key.toString() + " ");
			}
		}

	}

	/*
	 ** Вставка узла
	 */

	private Entry insertNode(K key, V value) {
		Entry current = root;
		Entry prev = nil;
		Entry newNode = new Entry(key, value, RED);
		if (root == nil) {
			root = newNode;
			return newNode;
		}
		while (current != nil) {
			prev = current;
			if (newNode.getKey().compareTo(current.getKey()) < 0)
				current = current.getLeft();
			else
				current = current.getRight();
		}
		if (newNode.getKey().compareTo(prev.getKey()) < 0)
			prev.setLeft(newNode);
		else
			prev.setRight(newNode);
		newNode.setParent(prev);
		return newNode;
	}

	public void add(K key, V value) {
		if (key == null || value == null)
			return ;

		Entry newNode = insertNode(key, value);
		balanceTreeAfterAdd(newNode);
	}

	/*
	** Повороты дерева вокруг узла
	 */

	private void rotateLeft(Entry node) {
		Entry pivot = node.getRight();
		Entry parent = node.getParent();

		pivot.setParent(parent);
		if (parent != nil) {
			if (parent.getLeft() == node)
				parent.setLeft(pivot);
			else
				parent.setRight(pivot);
		}
		else
			this.root = pivot;
		node.setRight(pivot.getLeft());
		if (pivot.getLeft() != nil && pivot.getLeft() != null)
			pivot.getLeft().setParent(node);
		node.setParent(pivot);
		pivot.setLeft(node);
	}

	private void rotateRight(Entry node) {
		Entry pivot = node.getLeft();
		Entry parent = node.getParent();

		pivot.setParent(parent);
		if (parent != nil) {
			if (parent.getLeft() == node)
				parent.setLeft(pivot);
			else
				parent.setRight(pivot);
		}
		else
			this.root = pivot;
		node.setLeft(pivot.getRight());
		if (pivot.getRight() != null && pivot.getRight() != nil)
			pivot.getRight().setParent(node);
		node.setParent(pivot);
		pivot.setRight(node);
	}

	/*
	** Баллансировка дерева после вставки узла
	 */

	private void whenChildAndParentSame(Entry node) {
		Entry g = node.getGrandFather();

		node.getParent().setColor(BLACK);
		g.setColor(RED);
		boolean i = node.isRight();
		boolean j = node.getParent().isRight();
		if (node.isLeft() && node.getParent().isLeft())
			rotateRight(g);
		else if (node.isRight() && node.getParent().isRight())
			rotateLeft(g);
	}

	private void whenChildAndParentOpposite(Entry node) {
		Entry p = node.getParent();

		if (node.isRight() && p.isLeft()) {
			rotateLeft(p);
			node = node.getLeft();
		}
		else  if (node.isLeft() && p.isRight()) {
			rotateRight(p);
			node = node.getRight();
		}
		whenChildAndParentSame(node);
	}

	private void whenParentAndUncleIsRed(Entry node) {
		Entry u = node.getUncle();
		Entry p;

		if (node.getParent().getColor() == RED) {
			if (u != nil && u.getColor() == RED) {
				node.getParent().setColor(BLACK);
				u.setColor(BLACK);
				p = node.getGrandFather();
				p.setColor(RED);
				whenNodeIsRoot(p);
			} else
				whenChildAndParentOpposite(node);
		}
	}

	private void whenNodeIsRoot(Entry node) {
		if (node.getParent() == nil)
			node.setColor(BLACK);
		else
			whenParentAndUncleIsRed(node);
	}
	private void balanceTreeAfterAdd(Entry node) {
		whenNodeIsRoot(node);
	}

	/*
	** Префиксная печать дерева
	 */

	private void printNodesPrefix(Entry node) {
		if (node != nil) {
			printNodesPrefix(node.getLeft());
			node.printNode();
			printNodesPrefix(node.getRight());
		}
	}

	public void printTreePrefix() {
		this.printNodesPrefix(this.root);
	}

}
