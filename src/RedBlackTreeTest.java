import static org.junit.Assert.assertEquals;
import org.junit.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class RedBlackTreeTest {
	private ByteArrayOutputStream outPrint = new ByteArrayOutputStream();
	private PrintStream sysOut = System.out;
	private RedBlackTree<Integer, Integer> tree;
	private Method insertNode;

	@Before
	public void initTest() throws NoSuchMethodException {
		tree = new RedBlackTree<>();
		insertNode = RedBlackTree.class.getDeclaredMethod("insertNode", Comparable.class, Object.class);
		insertNode.setAccessible(true);
		System.setOut(new PrintStream(outPrint));
	}

	@Test
	public void testInsertOneElemWithoutBalancingDescend() throws InvocationTargetException, IllegalAccessException {
		insertNode.invoke(tree, 1, 1);
		tree.printTreePrefix();
		assertEquals("R_1 ", outPrint.toString());
	}

	@Test
	public void testInsertTwoElemWithoutBalancingDescend() throws InvocationTargetException, IllegalAccessException {
		insertNode.invoke(tree, 1, 1);
		insertNode.invoke(tree, 2, 1);
		tree.printTreePrefix();
		assertEquals("R_1 R_2 ", outPrint.toString());
	}

	@Test
	public void testInsertThreeElemWithoutBalancingDescend() throws InvocationTargetException, IllegalAccessException {
		insertNode.invoke(tree, 3, 1);
		insertNode.invoke(tree, 2, 1);
		insertNode.invoke(tree, 1, 1);
		tree.printTreePrefix();
		assertEquals("R_1 R_2 R_3 ", outPrint.toString());
	}

	@Test
	public void testInsertOneElemWithoutBalancingAscend() throws InvocationTargetException, IllegalAccessException {
		insertNode.invoke(tree, 1, 1);
		tree.printTreePrefix();
		assertEquals("R_1 ", outPrint.toString());
	}

	@Test
	public void testInsertTwoElemWithoutBalancingAscend() throws InvocationTargetException, IllegalAccessException {
		insertNode.invoke(tree, 2, 1);
		insertNode.invoke(tree, 1, 1);
		tree.printTreePrefix();
		assertEquals("R_1 R_2 ", outPrint.toString());
	}

	@Test
	public void testInsertThreeElemWithoutBalancingAscend() throws InvocationTargetException, IllegalAccessException {
		insertNode.invoke(tree, 3, 1);
		insertNode.invoke(tree, 2, 1);
		insertNode.invoke(tree, 1, 1);
		tree.printTreePrefix();
		assertEquals("R_1 R_2 R_3 ", outPrint.toString());
	}

	@Test
	public void testAddFirstElemNotNull() {
		tree.add(10, 10);

		tree.printTreePrefix();
		assertEquals("B_10 ", outPrint.toString());
	}

	@Test
	public void testAddFirstElemNull() {
		tree.add(null, 10);

		tree.printTreePrefix();
		assertEquals("", outPrint.toString());
	}

	@Test
	public void testAddTwoElemDescend() {
		tree.add(20, 20);
		tree.add(10, 10);

		tree.printTreePrefix();
		assertEquals("R_10 B_20 ", outPrint.toString());
	}

	@Test
	public void testAddTwoElemAscend() {
		tree.add(10, 10);
		tree.add(20, 20);

		tree.printTreePrefix();
		assertEquals("B_10 R_20 ", outPrint.toString());
	}

	@Test
	public void testAddTwoElemWithNull() {
		tree.add(10, 10);
		tree.add(null, 20);

		tree.printTreePrefix();
		assertEquals("B_10 ", outPrint.toString());
	}

	@Test
	public void testAddElemWithNullValue() {
		tree.add(10, null);

		tree.printTreePrefix();
		assertEquals("", outPrint.toString());
	}

	@Test
	public void testAddThreeElemAscend() {
		tree.add(10, 10);
		tree.add(20, 20);
		tree.add(30,30);

		tree.printTreePrefix();
		assertEquals("R_10 B_20 R_30 ", outPrint.toString());
	}

	@Test
	public void testAddThreeElemDescend() {
		tree.add(30, 30);
		tree.add(20, 20);
		tree.add(10,10);

		tree.printTreePrefix();
		assertEquals("R_10 B_20 R_30 ", outPrint.toString());
	}

	@Test
	public void testRedNodeLeftRedParentRight() {
		tree.add(10, 10);
		tree.add(30, 30);
		tree.add(20,20);

		tree.printTreePrefix();
		assertEquals("R_10 B_20 R_30 ", outPrint.toString());
	}

	@Test
	public void testRedNodeRightRedParentLeft() {
		tree.add(30, 30);
		tree.add(10, 10);
		tree.add(20,20);

		tree.printTreePrefix();
		assertEquals("R_10 B_20 R_30 ", outPrint.toString());
	}

	@Test
	public void testEmptyTree() {
		tree.printTreePrefix();
		assertEquals("", outPrint.toString());
	}

	@Test
	public void testFourSameKeys() {
		tree.add(10, 10);
		tree.add(10, 10);
		tree.add(10, 10);
		tree.add(10, 10);
		tree.printTreePrefix();
		assertEquals("B_10 B_10 B_10 R_10 ", outPrint.toString());
	}

	@Test
	public void testColorChange() {
		tree.add(10, 1);
		tree.add(5, 1);
		tree.add(6, 1);
		tree.add(5, 1);
		tree.printTreePrefix();
		assertEquals("B_5 R_5 B_6 B_10 ", outPrint.toString());
	}

	@After
	public void resetOutputStream() {
		System.setOut(sysOut);
	}
}
