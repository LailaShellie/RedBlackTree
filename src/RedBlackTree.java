/*
** Красно-черное дерево
** Размещение элементов происходит по ключу
** Если ключ уже есть в дереве содержимое заменяется
 */

import java.util.Iterator;
import java.util.NoSuchElementException;

public class RedBlackTree<K extends Comparable<K>, V> implements Iterable<V>{
	private static final boolean RED = true;
	private static final boolean BLACK = false;

	private Entry root; // корень дерева
	private Entry nil; // заглушка
	private Entry cur; // текущий узел при обходе итератором

	private int size; // количество элементов в дереве

	public RedBlackTree() {
		this.nil = new Entry();
		this.nil.setParent(nil);
		this.nil.setLeft(nil);
		this.nil.setRight(nil);
		this.root = nil;
		this.cur = nil;
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
			this.color = BLACK;
		}

		/*
		 ** Конструктор инициализирующий узел
		 */

		public Entry(K key, V value, boolean color) {
			this.key = key;
			this.value = value;
			this.parent = nil;
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
		** Возвращает следующий узел по префиксному обходу
		 */

		private Entry getNextNodePrefix() {
			Entry tmp;
			Entry cur = this;
			if (cur.getRight() != nil) {
				tmp = cur.getRight();
				while (tmp.getLeft() != nil)
					tmp = tmp.getLeft();
				return tmp;
			}
			tmp = cur.getParent();
			while (tmp != nil && cur.isRight()) {
				cur = tmp;
				tmp = tmp.getParent();
			}
			return tmp;
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

	}

	/*
	 ** Вставка узла, если удалось вставить возвращает ссылку на вставленный элемент
	 ** Если ключ уже существует заменяет содержимое и возвращает null
	 */

	private Entry insertNode(K key, V value) {
		Entry current = root;
		Entry prev = nil;
		int res;

		Entry newNode = new Entry(key, value, RED);

		if (root == nil) {
			root = newNode;
			return newNode;
		}
		while (current != nil) {
			prev = current;
			res = newNode.getKey().compareTo(current.getKey());
			if (res == 0) {
				current.setValue(newNode.getValue());
				return null;
			}
			else if (res < 0)
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
		if (newNode != null) {
			size++;
			balanceTreeAfterAdd(newNode);
		}
	}

	/*
	** Ищет и возвращает элемент по ключу, если его нет возвращает null
	 */

	public V findElementByKey(K key) {
		Entry current = this.root;
		int res;

		while (current != nil) {
			res = key.compareTo(current.getKey());
			if (res == 0)
				return current.getValue();
			else if (res < 0)
				current = current.getLeft();
			else
				current = current.getRight();
		}
		return null;
	}

	/*
	 ** Возвращает количество элементов
	 */

	public int getSize() {
		return size;
	}

	/*
	 ** True если ключ содержится в дереве, false если нет
	 */

	public boolean containsKey(K key) {
		return findElementByKey(key) != null;
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
	** Находит самый левый(с минимальным ключом) узел
	 */

	private Entry findMin() {
		Entry cur = root;

		while (cur.getLeft() != null && cur.getLeft() != nil)
			cur = cur.getLeft();
		return cur;
	}

	@Override
	public Iterator<V> iterator() {
		return new Iterator<>() {
			@Override
			public boolean hasNext() {
				if (cur != nil) {
					Entry entry = cur.getNextNodePrefix();
					return entry != nil;
				}
				else
					return root != nil;
			}

			@Override
			public V next() {
				if (cur != nil)
					cur = cur.getNextNodePrefix();
				else
					cur = findMin();
				if (cur == nil)
					throw new NoSuchElementException();
				return cur.getValue();
			}
		};
	}

}
