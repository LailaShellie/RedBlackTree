import org.junit.*;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;

import static org.junit.Assert.*;

public class RedBlackTreeTest {
	private RedBlackTree<Integer, String> tree;
	private Iterator<String> iterator;

	/*
	** Приватные методы вложенного класса
	 */

	private Method getColor;
	private Method getParent;
	private Method getLeft;
	private Method getRight;
	private Method getBlackNodes;
	private Method isEmpty;

	/*
	 ** Приватные поля класса RedBlackTree
	 */

	private Field fieldRoot;
	private Field fieldNil;
	private Field fieldCur;

	private Boolean RED;
	private Boolean BLACK;

	@Before
	public void initTest() throws NoSuchFieldException, NoSuchMethodException, IllegalAccessException {
		Field red;

		tree = new RedBlackTree<>();

		fieldRoot = RedBlackTree.class.getDeclaredField("root");
		fieldRoot.setAccessible(true);
		fieldCur = RedBlackTree.class.getDeclaredField("cur");
		fieldCur.setAccessible(true);
		fieldNil = RedBlackTree.class.getDeclaredField("nil");
		fieldNil.setAccessible(true);

		red = RedBlackTree.class.getDeclaredField("RED");
		red.setAccessible(true);
		RED = (Boolean) red.get(tree);
		BLACK = !RED;

		/*
		** Получение методов вложенного класса
		 */

		Class<?> entry = fieldRoot.getType();

		getColor = entry.getDeclaredMethod("getColor");
		getColor.setAccessible(true);
		getParent = entry.getDeclaredMethod("getParent");
		getParent.setAccessible(true);
		getLeft = entry.getDeclaredMethod("getLeft");
		getLeft.setAccessible(true);
		getRight = entry.getDeclaredMethod("getRight");
		getRight.setAccessible(true);
		getBlackNodes = entry.getDeclaredMethod("countBlackNodes");
		getBlackNodes.setAccessible(true);
		isEmpty = entry.getDeclaredMethod("isEmpty");
		isEmpty.setAccessible(true);
	}

	@Test
	public void testFindByExistingKey() {
		int key = 0;

		tree.add(key, "Actual");
		tree.add(key + 1, "Element2");
		tree.add(key + 2, "Element3");
		assertEquals("Actual", tree.findElementByKey(key));
	}

	@Test
	public void testFindByKeyEmptyTree() {
		int key = 0;

		assertNull(tree.findElementByKey(key));
	}

	@Test
	public void testContainsExistingKey() {
		int key = 0;

		tree.add(key, "Element");
		assertTrue(tree.containsKey(key));
	}

	@Test
	public void testContainsNonExistingKey() {
		int key = 0;

		tree.add(key, "Element");
		assertFalse(tree.containsKey(key + 1));
	}

	@Test
	public void testContainsKeyEmptyTree() {
		int key = 0;

		assertFalse(tree.containsKey(key));
	}

	@Test
	public void testContainsNonExistingKeyEmptyTree() {
		int key = 0;

		assertFalse(tree.containsKey(key + 1));
	}

	@Test
	public void testRootColorAfterOneInsert() throws InvocationTargetException, IllegalAccessException {
		int key = 0;

		tree.add(key, "1");
		assertEquals(BLACK, getColor.invoke(fieldRoot.get(tree)));
	}

	@Test
	public void testRootColorAfterLeftRotate() throws InvocationTargetException, IllegalAccessException {
		int key = 0;

		tree.add(key - 1, "1");
		tree.add(key, "Root");
		tree.add(key + 1, "3");
		assertEquals("Root", tree.findElementByKey(key));
		assertEquals(BLACK, getColor.invoke(fieldRoot.get(tree)));
	}

	@Test
	public void testRootColorAfterRightRotate() throws InvocationTargetException, IllegalAccessException {
		int key = 0;

		tree.add(key + 1, "1");
		tree.add(key, "Root");
		tree.add(key - 1, "3");
		assertEquals("Root", tree.findElementByKey(key));
		assertEquals(BLACK, getColor.invoke(fieldRoot.get(tree)));
	}

	private boolean iterateAndCheckColors(RedBlackTree<Integer, String> tree) throws IllegalAccessException, InvocationTargetException {

		boolean curColor;

		/*
		 ** Цвета его потомков и родителя
		 */

		boolean parentColor;
		boolean leftColor;
		boolean rightColor;

		boolean ret = true; // флаг корректности выполнения теста

		iterator = tree.iterator();
		while (iterator.hasNext()) {

			curColor = (Boolean) getColor.invoke(fieldCur.get(tree)); // цвет текущего элемента

			/*
			 ** Получаем цвета его потомков и родителя
			 */

			parentColor = (Boolean) getColor.invoke(getParent.invoke(fieldCur.get(tree)));
			leftColor = (Boolean) getColor.invoke(getLeft.invoke(fieldCur.get(tree)));
			rightColor = (Boolean) getColor.invoke(getRight.invoke(fieldCur.get(tree)));

			if (curColor == RED) {

				/*
				 ** Проверяем есть у красного элемента красные соседи
				 */

				if (parentColor == RED || rightColor == RED || leftColor== RED) {
					ret = false;
					break;
				}
			}
			iterator.next();
		}
		return ret;
	}

	@Test
	public void testColorsAfterAscendSortInsert() throws IllegalAccessException, InvocationTargetException {
		int key = 0;

		tree.add(key++, "4");
		tree.add(key++, "5");
		tree.add(key++, "6");
		tree.add(key++, "7");
		tree.add(key++, "1");
		tree.add(key++, "2");
		tree.add(key, "3");

		assertTrue(iterateAndCheckColors(tree));
	}

	@Test
	public void testColorsAfterDescendSortInsert() throws IllegalAccessException, InvocationTargetException {
		int key = 0;

		tree.add(key--, "4");
		tree.add(key--, "5");
		tree.add(key--, "6");
		tree.add(key--, "7");
		tree.add(key--, "1");
		tree.add(key--, "2");
		tree.add(key, "3");

		assertTrue(iterateAndCheckColors(tree));
	}

	@Test
	public void testColorsAfterMixedSortInsert() throws IllegalAccessException, InvocationTargetException {
		int key = 0;

		tree.add(10, "10");
		tree.add(11, "11");
		tree.add(9, "6");
		tree.add(12, "7");
		tree.add(8, "1");
		tree.add(13, "2");
		tree.add(7, "3");

		assertTrue(iterateAndCheckColors(tree));
	}

	private boolean iterateAndCheckHeight(RedBlackTree<Integer, String> tree) throws IllegalAccessException, InvocationTargetException {
		int height = -1;
		int h;

		iterator = tree.iterator();
		while (iterator.hasNext()) {
			if ((Boolean) isEmpty.invoke(getLeft.invoke(fieldCur.get(tree))) ||
					(Boolean) isEmpty.invoke(getRight.invoke(fieldCur.get(tree)))) {
				if (height == -1) {
					height = (int) getBlackNodes.invoke(fieldCur.get(tree)) + 1;
				}
				else {
					h = (int) getBlackNodes.invoke(fieldCur.get(tree)) + 1;
					if (height != h) {
						return false;
					}
				}
			}
			iterator.next();
		}
		return true;
	}

	/*
	** Проверяет ченую высоту дерева
	 */

	@Test
	public void testBlackHeight() throws InvocationTargetException, IllegalAccessException {
		tree.add(1, "first");
		tree.add(2, "Second");
		tree.add(3, "third");
		tree.add(11, "first");
		tree.add(21, "Second");
		tree.add(31, "third");
		tree.add(12, "first");
		tree.add(23, "Second");
		tree.add(34, "third");
		assertTrue(iterateAndCheckHeight(tree));
	}

}
