package pair_programming;

import java.util.*;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Paths;
import javax.imageio.stream.FileImageInputStream;
import javax.swing.*;

class MatrixDG {

	public String[] mVexs; // 顶点集合
	public int[][] mMatrix; // 邻接矩阵

}

//图片到byte数组



class ScaleIcon implements Icon {

	private Icon icon = null;

	public ScaleIcon(Icon icon) {
		this.icon = icon;
	}

	@Override
	public int getIconHeight() {
		return icon.getIconHeight();
	}

	@Override
	public int getIconWidth() {
		return icon.getIconWidth();
	}

	public void paintIcon(Component c, Graphics g, int x, int y) {
		float wid = c.getWidth();
		float hei = c.getHeight();
		int iconWid = icon.getIconWidth();
		int iconHei = icon.getIconHeight();

		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g2d.scale(wid / iconWid, hei / iconHei);
		icon.paintIcon(c, g2d, 0, 0);
	}
}

public class pair_programming {
	
	public static byte[] image2byte(String path){
		  byte[] data = null;
		  FileImageInputStream input = null;
		  try {
		    input = new FileImageInputStream(new File(path));
		    ByteArrayOutputStream output = new ByteArrayOutputStream();
		    byte[] buf = new byte[1024];
		    int numBytesRead = 0;
		    while ((numBytesRead = input.read(buf)) != -1) {
		    output.write(buf, 0, numBytesRead);
		    }
		    data = output.toByteArray();
		    output.close();
		    input.close();
		  }
		  catch (FileNotFoundException ex1) {
		    ex1.printStackTrace();
		  }
		  catch (IOException ex1) {
		    ex1.printStackTrace();
		  }
		  return data;
		}

	// 返回位置
	public static int getPosition(MatrixDG Graph, String node) {
		for (int i = 0; i < Graph.mVexs.length; i++)
			if (Graph.mVexs[i].equals(node))
				return i;
		return -1;
	}

	public static void setmMatrix(MatrixDG Graph, int[][] mMatrix) {
		Graph.mMatrix = mMatrix;
	}

	// 打印矩阵队列图
	public static void print(MatrixDG Graph) {
		System.out.println("有向图的顶点：");
		for (int i = 0; i < Graph.mVexs.length; i++) {
			System.out.print(Graph.mVexs[i] + " ");
		}
		System.out.println("\n有向图的邻接矩阵：");
		for (int i = 0; i < Graph.mVexs.length; i++) {
			for (int j = 0; j < Graph.mVexs.length; j++)
				System.out.printf("%d ", Graph.mMatrix[i][j]);
			System.out.println();
		}
	}

	// 创建图
	public static void createDirectedGraph(MatrixDG Graph, String[] vex, String[] edges) {

		// 初始化"顶点数"和"边数"
		int vlen = vex.length;
		int elen = edges.length - 1;

		// 初始化"顶点"
		Graph.mVexs = new String[vlen];
		for (int i = 0; i < Graph.mVexs.length; i++)
			Graph.mVexs[i] = vex[i];

		// 初始化"边"
		setmMatrix(Graph, new int[vlen][vlen]);
		for (int i = 0; i < elen; i++) {
			// 读取边的起始顶点和结束顶点
			int p1 = getPosition(Graph, edges[i]);
			int p2 = getPosition(Graph, edges[i + 1]);

			Graph.mMatrix[p1][p2]++;
		}
	}

	// 图的可视化
	public static void showDirectedGraph(MatrixDG Graph) {
		GraphViz gv = new GraphViz();
		gv.addln(gv.start_graph());
		for (int i = 0; i < Graph.mVexs.length; i++)
			for (int j = 0; j < Graph.mVexs.length; j++)
				if (Graph.mMatrix[i][j] != 0)
					gv.addln(Graph.mVexs[i] + "->" + Graph.mVexs[j] + "[label=\"" + Graph.mMatrix[i][j] + "\"]" + ";");

		gv.addln(gv.end_graph());
		// System.out.println(gv.getDotSource());
		gv.getDotSource();
		String type = "png";
		File out = new File("C:\\temp\\graphOut." + type);
		gv.writeGraphToFile(gv.getGraph(gv.getDotSource(), type), out);
		byte[] image = image2byte("C:\\temp\\graphOut.png");		
		ScaleIcon icon = new ScaleIcon(new ImageIcon(image));
		JLabel label = new JLabel(icon);
		JFrame frame = new JFrame();
		
		int v = ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED;
		int h = ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED;
		JScrollPane jsp = new JScrollPane(label, v, h);
		
		frame.getContentPane().add(jsp, BorderLayout.CENTER);
		// frame.getContentPane().add(new JButton("click"),BorderLayout.NORTH);
		frame.setSize(icon.getIconWidth(),icon.getIconHeight());
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setVisible(true);
	}

	// 查询桥接词
	public static String queryBridgeWords(MatrixDG Graph, String word1, String word2) {
		String wordsReurnStr = "";
		boolean bridgeword = false;
		int[] word3 = new int[Graph.mVexs.length];
		int word1pos = getPosition(Graph, word1);
		int word2pos = getPosition(Graph, word2);
		if (word1pos == -1) {
			if (word2pos == -1) {
				System.out.println("No \"" + word1 + " \"and \"" + word2 + " \" in graph!");
				return "";
			} else {
				System.out.println("No \"" + word1 + " \" in graph!");
				return "";
			}
		} else if (word2pos == -1) {
			System.out.println("No \"" + word2 + " \" in graph!");
			return "";
		} else {
			for (int i = 0; i < Graph.mVexs.length; i++) {
				if ((Graph.mMatrix[word1pos][i]) * (Graph.mMatrix[i][word2pos]) != 0) {
					word3[i] = 1;
					bridgeword = true;
				}
			}
		}
		if (bridgeword) {
			System.out.print("The bridge word from \"" + word1 + " \"to \"" + word2 + " \" :");
			for (int i = 0; i < Graph.mVexs.length; i++)
				if (word3[i] == 1)
					wordsReurnStr += Graph.mVexs[i] + " ";
		} else
			System.out.println("No bridge word from \"" + word1 + " \"to \"" + word2 + " \" !");
		return wordsReurnStr;
	}

	public static int insertBridgeWordsSearch(MatrixDG Graph, String word1, String word2) {
		ArrayList<Integer> wordmarked = new ArrayList<Integer>();
		Random random1 = new Random();
		boolean judgement = false;
		int word1pos = getPosition(Graph, word1);
		int word2pos = getPosition(Graph, word2);
		int num;
		if (word1pos == -1 || word2pos == -1)
			return -1;
		else {
			for (int i = 0; i < Graph.mVexs.length; i++) {
				if ((Graph.mMatrix[word1pos][i]) * (Graph.mMatrix[i][word2pos]) != 0) {
					wordmarked.add(i);
					judgement = true;
				}
			}
			if (!judgement)
				return -1;
			else
				num = (random1.nextInt(100)) % (wordmarked.size());
			return (int) wordmarked.get(num);
		}
	}

	public static String generateNewText(MatrixDG Graph, String[] strGeneSplited) {
		int mark = -1;
		String wordsReurnStr = "";
		System.out.print("插入桥接词后的语句：\n" + strGeneSplited[0] + " ");
		for (int i = 0; i < strGeneSplited.length - 1; i++) {
			mark = insertBridgeWordsSearch(Graph, strGeneSplited[i], strGeneSplited[i + 1]);
			if (mark != -1) {
				wordsReurnStr += Graph.mVexs[mark] + " ";
			}
			wordsReurnStr += strGeneSplited[i + 1] + " ";
		}
		return wordsReurnStr;
	}

	public static void Floyd(MatrixDG Graph, int vexlen, int[][] Path, int[][] Distance) {
		for (int i = 0; i < vexlen; i++)
			for (int j = 0; j < vexlen; j++) {
				if (Graph.mMatrix[i][j] != 0)
					Path[i][j] = j;
				else
					Path[i][j] = -1;
				Distance[i][j] = Graph.mMatrix[i][j];
			}
		for (int k = 0; k < vexlen; k++) {
			for (int i = 0; i < vexlen; i++)
				for (int j = 0; j < vexlen; j++) {
					if (i == j || j == k || i == k)
						continue;
					if (Distance[i][k] != 0 && Distance[k][j] != 0)
						if (Distance[i][k] + Distance[k][j] < Distance[i][j] || Distance[i][j] == 0) {
							Distance[i][j] = Distance[i][k] + Distance[k][j];
							Path[i][j] = Path[i][k];
						}
				}
		}
	}

	// 计算最短路径
	public static String calcShortestPath(MatrixDG Graph, String word1, String word2) {
		int[][] Path = new int[Graph.mVexs.length][Graph.mVexs.length];
		int[][] Distance = new int[Graph.mVexs.length][Graph.mVexs.length];
		String wordsReurnStr = "";
		Floyd(Graph, Graph.mVexs.length, Path, Distance);
		int word1pos = getPosition(Graph, word1);
		int word2pos = getPosition(Graph, word2);
		if (word1pos == -1 || word2pos == -1) {
			System.out.println("输入的词不存在于图中！");
			return "";
		}
		int pathScanner = Path[word1pos][word2pos];
		if (pathScanner == -1)
			System.out.println("起点 \"" + word1 + " \"到终点 \"" + word2 + " \"无路径。");
		else {
			if (word1pos != word2pos) {
				System.out.print("起点 \"" + word1 + " \"到终点 \"" + word2 + " \"最短");
				wordsReurnStr += word1;
				while (pathScanner != word2pos) {
					wordsReurnStr += "->" + Graph.mVexs[pathScanner];
					pathScanner = Path[pathScanner][word2pos];
				}
				wordsReurnStr += "->" + word2;
				System.out.println("路径长度为：" + Distance[word1pos][word2pos]);
			}
		}
		return wordsReurnStr;
	}

	public static void showDirectedGraph(MatrixDG Graph, String[] shortest) {
		GraphViz shortestG = new GraphViz();
		shortestG.addln(shortestG.start_graph());

		int[][] Matrix = new int[Graph.mVexs.length][Graph.mVexs.length];
		for (int i = 0; i < Graph.mVexs.length; i++) {
			for (int j = 0; j < Graph.mVexs.length; j++) {
				Matrix[i][j] = Graph.mMatrix[i][j];
			}
		}

		for (int i = 0; i < shortest.length - 1; i++) {
			Matrix[getPosition(Graph, shortest[i])][getPosition(Graph, shortest[i + 1])] = -1;
		}

		for (int i = 0; i < Graph.mVexs.length; i++)
			for (int j = 0; j < Graph.mVexs.length; j++)
				if (Matrix[i][j] > 0)
					shortestG.addln(
							Graph.mVexs[i] + "->" + Graph.mVexs[j] + "[label=\"" + Graph.mMatrix[i][j] + "\"]" + ";");
				else if (Matrix[i][j] < 0)
					shortestG.addln(Graph.mVexs[i] + "->" + Graph.mVexs[j] + "[color=\"red\",label=\""
							+ Graph.mMatrix[i][j] + "\"];");

		shortestG.addln(shortestG.end_graph());
		shortestG.getDotSource();
		String type1 = "png";
		File out1 = new File("C:\\temp\\shortestPathOut." + type1);
		shortestG.writeGraphToFile(shortestG.getGraph(shortestG.getDotSource(), type1), out1);
		byte[] image = image2byte("C:\\temp\\shortestPathOut.png");		
		ScaleIcon icon = new ScaleIcon(new ImageIcon(image));
		JLabel label = new JLabel(icon);
		label.repaint();
		label.updateUI();
		label.setVisible(true);
		JFrame frame = new JFrame();
		int v = ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED;
		int h = ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED;
		
		JScrollPane jsp = new JScrollPane(label, v, h);
		frame.getContentPane().add(jsp, BorderLayout.CENTER);
		// frame.getContentPane().add(new JButton("click"),BorderLayout.NORTH);
		frame.setSize(icon.getIconWidth(),icon.getIconHeight());
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setVisible(true);
		System.out.println("\nshortestPathOut.png输出成功。");
	}

	public static String randomWalk(MatrixDG Graph) {
		Random random = new Random();
		String wordsReurnStr = "";
		int[][] Matrix = new int[Graph.mVexs.length][Graph.mVexs.length];
		for (int i = 0; i < Graph.mVexs.length; i++) {
			for (int j = 0; j < Graph.mVexs.length; j++) {
				Matrix[i][j] = Graph.mMatrix[i][j];
			}
		}
		int numToGo = (random.nextInt(100)) % Graph.mVexs.length;
		System.out.print(Graph.mVexs[numToGo]);
		boolean rowMark = true;
		while (rowMark) {
			rowMark = false;
			for (int i = 0; i < Graph.mVexs.length; i++)
				if (Matrix[numToGo][i] > 0) {
					rowMark = true;
					break;
				}
			int mark = numToGo;
			ArrayList<Integer> canChoose = new ArrayList<Integer>();
			for (int i = 0; i < Graph.mVexs.length; i++) {
				if (Matrix[numToGo][i] != 0) {
					canChoose.add(i);
				}
			}
			if (canChoose.size() == 0)
				break;
			numToGo = (int) canChoose.get((random.nextInt(100)) % (canChoose.size()));
			if (Matrix[mark][numToGo] == -1) {
				wordsReurnStr += "->" + Graph.mVexs[numToGo];
				break;
			}
			Matrix[mark][numToGo] = -1;
			wordsReurnStr += "->" + Graph.mVexs[numToGo];
		}
		return wordsReurnStr;
	}

	public static void main(String[] args) {
		System.out.println("请输入文件名：");
		Scanner input = new Scanner(System.in);
		String filename = input.next();
		Scanner fileread = null;
		try {
			fileread = new Scanner(Paths.get(filename));
		} catch (IOException e) {
			System.out.println("file not found");
			System.exit(0);
		}

		String str = "";
		while (fileread.hasNextLine())
			str += fileread.nextLine();
		System.out.println(str);// test
		String strNew = str.replaceAll("[^a-zA-Z]", " ").toLowerCase();
		Scanner strNewTrans = new Scanner(strNew);
		List<String> listEdges = new ArrayList<String>();
		List<String> listVexs = new ArrayList<String>();
		while (strNewTrans.hasNext()) {
			String listTemp = strNewTrans.next();
			listEdges.add(listTemp);
			if (!listVexs.contains(listTemp))
				listVexs.add(listTemp);
		}
		strNewTrans.close();
		Object[] listEdges0 = listEdges.toArray();
		String[] edges = new String[listEdges0.length];
		for (int i = 0; i < listEdges0.length; i++) {
			edges[i] = listEdges0[i].toString();
		}
		Object[] listVexs0 = listVexs.toArray();
		String[] vex = new String[listVexs0.length];
		for (int i = 0; i < listVexs0.length; i++) {
			vex[i] = listVexs0[i].toString();
		}

		MatrixDG Graph = new MatrixDG();
		String choice = "-1";
		while (!choice.equals("0")) {
			System.out.println("功能选择：");
			System.out.println(">>1.生成有向图");
			System.out.println(">>2.图的可视化");
			System.out.println(">>3.查询桥接词");
			System.out.println(">>4.生成新文本");
			System.out.println(">>5.求最短路径");
			System.out.println(">>6.随机游走");
			System.out.println(">>0.停止工作");
			System.out.println("请选择功能：");
			String word1, word2;
			choice = input.next();

			switch (choice) {
			default:
				System.out.println();
				break;
			case "1":
				// 有向图邻接矩阵创建与输出
				createDirectedGraph(Graph, vex, edges);
				System.out.println("有向图生成成功！");
				// print(Graph);
				break;
			case "2":
				// 图的可视化
				System.out.println("图的可视化：");
				showDirectedGraph(Graph);

				break;
			case "3":
				// 查询桥接词
				System.out.println("查询桥接词，请输入word1，word2：");
				word1 = input.next();
				word2 = input.next();
				System.out.println(queryBridgeWords(Graph, word1, word2));
				break;
			case "4":
				// 桥接词插入
				System.out.println("\n桥接词插入，请输入要插入桥接词的语句：");
				input.nextLine();
				String strGene = input.nextLine();
				String[] strGeneSplited = strGene.split(" ");
				System.out.println(generateNewText(Graph, strGeneSplited));
				break;
			case "5":
				// 最短路径
				System.out.println("\n最短路径查询，请输入word1，word2：");
				input.nextLine();
				String words = input.nextLine();
				String[] wordsA = words.split(" ");
				if (wordsA.length == 2) {
					word1 = wordsA[0];
					word2 = wordsA[1];
					String[] shortest = calcShortestPath(Graph, word1, word2).split("->");
					System.out.print(shortest[0]);
					for (int i = 1; i < shortest.length; i++) {
						System.out.print("->" + shortest[i]);
					}
					showDirectedGraph(Graph, shortest);// 最短路径标注
					System.out.println();
				} else if (wordsA.length == 1) {
					word1 = wordsA[0];
					for (int i = 0; i < vex.length; i++) {
						if (!word1.equals(vex[i])) {
							System.out.println(calcShortestPath(Graph, word1, vex[i]));
							System.out.println();
						}
					}
				} else
					System.out.println("输入错误！");
				break;
			case "6":
				// 随机游走
				System.out.println("\n随机游走：");
				System.out.println(randomWalk(Graph));
				System.out.println();
				break;
			}
		}
		System.out.println("已停止工作。");
		input.close();
	}
}
