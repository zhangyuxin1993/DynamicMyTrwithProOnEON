package MainFunction;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;

import demand.Request;
import general.Constant;
import general.Time;
import general.file_out_put;
import network.Layer;
import network.Link;
import network.Network;
import network.NodePair;
import randomfunctions.randomfunction;
import subgraph.LinearRoute;

public class Mymain {
	public static String OutFileName = "D:\\zyx\\programFile\\RegwithProandTrgro\\USNET.dat";
	// public static String OutFileName = "F:\\zyx\\programFile\\0.09.dat";
	public static float threshold = (float) 0.01;

	public static void main(String[] args) throws IOException {
		// String TopologyName = "F:/zyx/Topology/cost239.csv";
		String TopologyName = "D:/zyx/Topology/cost239.csv";
		Onlyfortest ot = new Onlyfortest();
		HashMap<String, NodePair> Readnodepairlist = new HashMap<String, NodePair>();
		HashMap<String, NodePair> RadomNodepairlist = new HashMap<String, NodePair>();
		ArrayList<NodePair> nodepairlist = new ArrayList<>();
		ArrayList<WorkandProtectRoute> wprlist = new ArrayList<>();

		file_out_put file_io = new file_out_put();
		// 产生的节点对之间的容量(int)(Math.random()*(2*Constant.AVER_DEMAND-20));
		Network network = new Network("ip over EON", 0, null);
		network.readPhysicalTopology(TopologyName);
		network.copyNodes();
		network.createNodepair();// 每个layer都生成节点对 产生节点对的时候会自动生成nodepair之间的demand
		// **(现在随机产生demand 已经注释)

		Layer iplayer = network.getLayerlist().get("Layer0");
		Layer oplayer = network.getLayerlist().get("Physical");
		// 以下可以读取表格中的业务
		ReadDemand rd = new ReadDemand();
		// nodepairlist=rd.readDemand(iplayer, "f:/ZYX/cost239Traffic.csv");
		nodepairlist = rd.readDemand(iplayer, "D:/cost239Traffic.csv");
		for (NodePair nodepair : nodepairlist) {
			System.out.println(nodepair.getName() + "  " + nodepair.getTrafficdemand());
			Readnodepairlist.put(nodepair.getName(), nodepair);
		}
		iplayer.setNodepairlist(Readnodepairlist);

		// 以下可以随机产生节点对
		// DemandRadom dr=new DemandRadom();
		// RadomNodepairlist=dr.demandradom(100,TopologyName,iplayer);//随机产生结对对并且产生业务量
		// iplayer.setNodepairlist(RadomNodepairlist);
		// int p=0;
		// HashMap<String, NodePair> testmap3 = iplayer.getNodepairlist();
		// Iterator<String> testiter3 = testmap3.keySet().iterator();
		// while (testiter3.hasNext()) {
		// p++;
		// NodePair node = (NodePair) (testmap3.get(testiter3.next()));
		//// file_io.filewrite2(OutFileName, "随机产生节点对为 "+p+" "+node.getName()+"
		// 流量为 "+ node.getTrafficdemand());
		// file_io.filewrite2("F:\\zyx\\programFile\\nodePair.dat",
		// node.getName());
		// file_io.filewrite("F:\\zyx\\programFile\\Demand.dat",
		// node.getTrafficdemand());
		// }

		ArrayList<NodePair> demandlist = Rankflow(iplayer);
		float Average = Findalowthreshold(demandlist);
		// for(NodePair no:demandlist){
		// file_io.filewrite2(OutFileName, "demand "+no.getName());
		// }

		// 动态参数设置
		randomfunction rndfunction = new randomfunction();
		int[] traffic_type = { 1, 1, 1, 10, 10, 10, 10, 10, 25, 25 };
		Random rnd_time = new Random(1);
		Random rnd_rate = new Random(3);

		double[] a = { 0.5, 20, 30, 40, 50, 60, 70 };
		for (int k1 = 0; k1 < 1; k1++) {// 仿真循环k1次
			double erlang_load = a[k1];
			ArrayList<Request> requestlist = new ArrayList<Request>();
			for (int n = 0; n < demandlist.size(); n++) {
				NodePair nodepair = demandlist.get(n);
				Time time = new Time(nodepair.getName(), 1 / erlang_load * rndfunction.expdev(rnd_time), 1, null);// 1为建立还是释放标志
				// log函数 log函数的随机数每次产生的不同 规定了到达时间
				double generatetime = time.getTime();
				double departtime = generatetime + rndfunction.expdev(rnd_time);
				int r = rnd_rate.nextInt(10); // 返回一个大于0 而小于10的随机整数
				int demand = traffic_type[r];
				Request ArrRequest = new Request(nodepair, demand, generatetime, departtime, Constant.ARRIVAL);
				InsertRequest(requestlist, ArrRequest);// 以上配置了Request的到达离开时间
														// 业务量并且加入Request列表
			}
			// 以上建立一个RequestList（由nodepairlist转化而来）

			// 以下开始仿真
			int total_request = 1;
			int total_blocking=0;
			Request currentrequest = null;
			while ((total_request <= Constant.SIM_ROUND) && (requestlist.size() > 0)) {// 最大的仿真次数
				boolean success = false;
				currentrequest = requestlist.get(0);
				NodePair nodepair = currentrequest.getNodepair();
				int currentDemand = currentrequest.getdemand();
				if (currentrequest.getRequesttype() == Constant.ARRIVAL) {
					Request Deprequest = new Request(nodepair, currentDemand, currentrequest.getArrivalTime(),
							currentrequest.getDepartTime(), Constant.DEPARTURE);// 当到达的业务到达时,同时建立离开的业务

					System.out
							.println("The name of the nodepair " + nodepair.getName() + "   仿真第" + total_request + "次");
					System.out.println("Request=" + currentrequest.getNodepair().getName() + " ,traffic="
							+ currentrequest.getdemand()+" ,at="+ currentrequest.getArrivalTime() + " ,dt=" + currentrequest.getDepartTime() );
					total_request++;
					if (total_request % 10000 == 0)
						System.out.println(
								"The name of the nodepair " + nodepair.getName() + "   仿真第" + total_request + "次");

					// 执行主要任务
					MajorMethod mm = new MajorMethod();
					success = mm.majormethod(nodepair, iplayer, oplayer, Average);
					if (success)
						InsertRequest(requestlist, Deprequest);
					//确定下次业务到来时间
					int r = rnd_rate.nextInt(10);
					int demand = traffic_type[r];
					double next_arrivaltime = currentrequest.getArrivalTime()+ 1 / erlang_load * rndfunction.expdev(rnd_time);
					double next_departtime = next_arrivaltime + rndfunction.expdev(rnd_time);
					Request NextArrRequ = new Request(nodepair, demand, next_arrivaltime, next_departtime,Constant.ARRIVAL);
					System.out.println("下次到达业务 =" + NextArrRequ.getNodepair().getName() + "traffic ="
							+ NextArrRequ.getdemand()+ " ,at="+ NextArrRequ.getArrivalTime() + " ,dt=" + NextArrRequ.getDepartTime() );
					InsertRequest(requestlist, NextArrRequ);
					requestlist.remove(0);
				}
				//以上为处理到达业务 之后处理离开业务（如果保护建立不成功 那这个业务就算失败 要释放工作占用的FS）
				
				if(currentrequest.getRequesttype() == Constant.DEPARTURE){
					RequestRelease rr=new RequestRelease();
					rr.requestrelease();
					requestlist.remove(currentrequest);
					//这里写程序释放该Request占用的FS以及再生器
				}
			}
			//检测阻塞率
			
			double result=(double)total_blocking/Constant.SIM_ROUND;//阻塞率：阻塞掉的业务个数比上所有仿真业务次数
//			double rate_blocking=(double)total_block_rate/total_request_rate;//则阻塞率的另一种算法：阻塞掉的rate/全部需要的rate
//			（可以使用阻塞掉的demand/全部需要的demand）
			System.out.println("event blocking:"+ total_blocking);
			System.out.println("block_probility:"+ result);
		}

		/*
 以下为测试
		System.out.println();
		System.out.println();
		file_io.filewrite2(OutFileName, "");
		file_io.filewrite2(OutFileName, "");
		System.out.println("业务个数：" + wprlist.size());
		file_io.filewrite2(OutFileName, "业务个数：" + wprlist.size());

		int demandnum = 0, TotalWorkRegNum = 0, TotalWorkIPReg = 0, TotalProRegNum = 0, TotalProIPReg = 0;
		ArrayList<Regenerator> reglist = new ArrayList<>();
		for (WorkandProtectRoute wpr : wprlist) {
			demandnum++;
			file_io.filewrite2(OutFileName, "业务：" + demandnum + "  " + wpr.getdemand().getName());
			file_io.filewrite_without(OutFileName, "工作路径：");
			for (Link link : wpr.getworklinklist()) {
				file_io.filewrite_without(OutFileName, link.getName() + "     ");
			}
			file_io.filewrite2(OutFileName, " ");
			// 工作路径放置再生器
			if (wpr.getdemand().getFinalRoute() != null) {// 说明该链路需要放置再生器
				RouteAndRegPlace FinalRoute = wpr.getdemand().getFinalRoute();
				file_io.filewrite_without(OutFileName, "工作路径放置再生器的位置为：");
				for (int reg : FinalRoute.getregnode()) {
					TotalWorkRegNum++;
					file_io.filewrite_without(OutFileName, reg + "  ");
				}
				file_io.filewrite2(OutFileName, "");
				if (FinalRoute.getIPRegnode() != null) {
					file_io.filewrite_without(OutFileName, "工作路径放置IP再生器的位置为：");
					for (int reg : FinalRoute.getIPRegnode()) {
						TotalWorkIPReg++;
						file_io.filewrite_without(OutFileName, reg + "  ");
					}
				}
			} else {
				file_io.filewrite2(OutFileName, "该工作链路不需要放置再生器");
			}

			file_io.filewrite2(OutFileName, " ");
			file_io.filewrite_without(OutFileName, "保护路径：");
			for (Link link : wpr.getprolinklist()) {
				file_io.filewrite_without(OutFileName, link.getName() + "     ");
			}

			file_io.filewrite2(OutFileName, "");
			file_io.filewrite_without(OutFileName, "保护路径放置共享再生器节点：");
			for (Regenerator reg : wpr.getsharereglist()) {
				reg.setPropathNum(reg.getPropathNum() + 1);
				if (!reglist.contains(reg)) {
					reglist.add(reg);
				}
				if (reg.getNature() == 0)
					file_io.filewrite_without(OutFileName,
							reg.getnode().getName() + "     " + "再生器在节点上的序号: " + reg.getindex() + " 是OEO再生器  ");

				if (reg.getNature() == 1)
					file_io.filewrite_without(OutFileName,
							reg.getnode().getName() + "     " + "再生器在节点上的序号: " + reg.getindex() + " 是IP再生器  ");
			}

			file_io.filewrite2(OutFileName, "");
			file_io.filewrite_without(OutFileName, "保护路径放置新再生器节点：");

			for (Regenerator reg : wpr.getnewreglist()) {
				reg.setPropathNum(reg.getPropathNum() + 1);
				if (!reglist.contains(reg)) {
					TotalProRegNum++;
					reglist.add(reg);
				}
				if (reg.getNature() == 0)
					file_io.filewrite_without(OutFileName,
							reg.getnode().getName() + "     " + "再生器在节点上的序号: " + reg.getindex() + " 是OEO再生器  ");

				if (reg.getNature() == 1) {
					file_io.filewrite_without(OutFileName,
							reg.getnode().getName() + "     " + "再生器在节点上的序号: " + reg.getindex() + " 是IP再生器  ");
					TotalProIPReg++;
				}

			}
			file_io.filewrite2(OutFileName, " ");

			// 测试共享个数
			// for(Regenerator reg:reglist){
			// file_io.filewrite2(OutFileName,reg.getnode().getName() + "
			// "+"再生器在节点上的序号:"+reg.getindex()+"
			// "+"该再生器已经被"+reg.getpropathNum()+"条路径共享");
			// }

			file_io.filewrite2(OutFileName, "");
			// if(wpr.getregthinglist()!=null){
			// for(int t:wpr.getregthinglist().keySet()){
			// file_io.filewrite2(OutFileName, "hashmap里面的键 "+t+"
			// 对应的节点为："+wpr.getregthinglist().get(t).getnode().getName());
			// }
			// file_io.filewrite2(OutFileName, "");
			// }
			// else{
			// file_io.filewrite2(OutFileName, "该业务保护路径不需要再生器");
			// }
			// file_io.filewrite2(OutFileName, "");

			// ArrayList<FSshareOnlink>
			// FSassignOneachLink=wpr.getFSoneachLink();
			// file_io.filewrite2(OutFileName, "此时的request为"+
			// wpr.getrequest().getNodepair().getName()+"分配保护路径FS如下");

			// if(FSassignOneachLink!=null){
			// for(FSshareOnlink fsassignoneachlink: FSassignOneachLink){
			// file_io.filewrite_without(OutFileName,
			// "链路"+fsassignoneachlink.getlink().getName()+"上分配的FS为 ");
			// for(int fs:fsassignoneachlink.getslotIndex()){
			// file_io.filewrite_without(OutFileName, fs+" ");
			// }
			// file_io.filewrite2(OutFileName, "");
			// }
			// }
			// file_io.filewrite2(OutFileName, "");
			// if(FSassignOneachLink==null){
			// file_io.filewrite2(OutFileName, "该保护路径在IP层grooming成功");
			// }

			// HashMap<String, Node> testmap2 = oplayer.getNodelist();
			// Iterator<String> testiter2 = testmap2.keySet().iterator();
			// while (testiter2.hasNext()) {
			// Node node = (Node) (testmap2.get(testiter2.next()));
			// file_io.filewrite2(OutFileName,
			// node.getName()+"上面再生器的个数："+node.getregnum());
			// }

		}
		file_io.filewrite2(OutFileName, "   ");
		file_io.filewrite2(OutFileName, "工作路径放置的再生器个数为：" + TotalWorkRegNum);
		file_io.filewrite2(OutFileName, "工作路径放置的IP再生器个数为：" + TotalWorkIPReg);
		float TotalWorkCost = 10 * (TotalWorkRegNum - TotalWorkIPReg) + 13 * TotalWorkIPReg;
		file_io.filewrite2(OutFileName, "工作路径再生器cost为：" + TotalWorkCost);
		file_io.filewrite2(OutFileName, "保护路径放置的再生器个数为：" + TotalProRegNum);
		file_io.filewrite2(OutFileName, "保护路径放置的IP再生器个数为：" + TotalProIPReg);
		float TotalProCost = 10 * (TotalProRegNum - TotalProIPReg) + 13 * TotalProIPReg;
		file_io.filewrite2(OutFileName, "工作路径再生器cost为：" + TotalProCost);
		// file_io.filewrite2(OutFileName, "");
		// file_io.filewrite2(OutFileName, "grooming的检测");
		// HashMap<String, Link> testmap4 = iplayer.getLinklist();
		// Iterator<String> testiter4 = testmap4.keySet().iterator();
		// while (testiter4.hasNext()) {
		// Link link=(Link) (testmap4.get(testiter4.next()));
		// file_io.filewrite2(OutFileName, "IP层上的链路："+link.getName());
		// ArrayList<VirtualLink> vlinklist=link.getVirtualLinkList();
		// for(VirtualLink vlink:vlinklist){
		// file_io.filewrite2(OutFileName,
		// "对应的虚拟链路："+vlink.getSrcnode()+"-"+vlink.getDesnode()+" 性质为："+
		// vlink.getNature());
		// file_io.filewrite2(OutFileName,
		// "虚拟链路上剩余的容量："+vlink.getRestcapacity());
		// }
		// }

		// file_io.filewrite2(OutFileName, "");
		// HashMap<String, Node> testmap2 = oplayer.getNodelist();
		// Iterator<String> testiter2 = testmap2.keySet().iterator();
		// while (testiter2.hasNext()) {
		// Node node = (Node) (testmap2.get(testiter2.next()));
		// file_io.filewrite2(OutFileName,
		// node.getName()+"上面再生器的个数："+node.getregnum());
		// }

		System.out.println();
		System.out.println("Finish");
		file_io.filewrite2(OutFileName, "");
		file_io.filewrite2(OutFileName, "Finish");
 */
	}
	//main函数结束
	public static float Findalowthreshold(ArrayList<NodePair> demandList) {
		int lastNum = demandList.size() / 5;
		int TotalTraffic = 0;
		for (int n = 0; n < lastNum; n++) {
			TotalTraffic = TotalTraffic + demandList.get(demandList.size() - 1 - lastNum).getTrafficdemand();
		}
		float Average = TotalTraffic / lastNum;
		return Average;
	}

	public static ArrayList<NodePair> Rankflow(Layer IPlayer) {
		ArrayList<NodePair> nodepairlist = new ArrayList<NodePair>(2000);
		HashMap<String, NodePair> map3 = IPlayer.getNodepairlist();
		Iterator<String> iter3 = map3.keySet().iterator();
		while (iter3.hasNext()) {
			NodePair np = (NodePair) (map3.get(iter3.next()));
			if (nodepairlist.size() == 0)
				nodepairlist.add(np);
			else {
				boolean insert = false;
				for (int i = 0; i < nodepairlist.size(); i++) {
					int m_flow = np.getTrafficdemand();
					int n_flow = nodepairlist.get(i).getTrafficdemand();

					if (m_flow > n_flow) {
						nodepairlist.add(i, np);
						insert = true;
						break;
					}

				}

				if (insert == false)
					nodepairlist.add(np);
			}
		}
		return nodepairlist;
	}

	public ArrayList<Integer> spectrumallocationOneRoute(Boolean routeflag, LinearRoute route, ArrayList<Link> linklist,
			int slotnum) {
		ArrayList<Link> linklistOnroute = new ArrayList<Link>();
		if (routeflag) {
			linklistOnroute = route.getLinklist();
		} else {
			linklistOnroute = linklist;
		}
		for (Link link : linklistOnroute) {
			link.getSlotsindex().clear();

			for (int start = 0; start < link.getSlotsarray().size() - slotnum; start++) {
				int flag = 0;
				for (int num = start; num < slotnum + start; num++) {// 分配的FS必须是连续的
					if (link.getSlotsarray().get(num).getoccupiedreqlist().size() != 0) {// 该波长已经被占用
						flag = 1;
						break;
					}
				}
				if (flag == 0) {
					link.getSlotsindex().add(start);// 查找可用slot的起点
				}
			}
		} // 以上所有的link分配完

		Link firstlink = linklistOnroute.get(0);
		ArrayList<Integer> sameindex = new ArrayList<Integer>();
		sameindex.clear();

		for (int s = 0; s < firstlink.getSlotsindex().size(); s++) {
			int index = firstlink.getSlotsindex().get(s);
			int flag = 1;

			for (Link otherlink : linklistOnroute) {
				if (otherlink.getName().equals(firstlink.getName()))
					continue;
				if (!otherlink.getSlotsindex().contains(index)) {
					flag = 0;
					break;
				}
			}
			if (flag == 1) {
				sameindex.add(index); // 挑选出该路径上所有link共同的slot start数
			}
		}
		// 测试频谱分配问题
		// for (Link link : linklistOnroute) {
		// System.out.println("");
		// System.out.println("测试频谱分配：");
		// System.out.println("链路： "+link.getName()+"
		// "+link.getSlotsindex().size());
		// }
		return sameindex;
	}

	public static void InsertRequest(ArrayList<Request> RequestList, Request NewRequest) {// 按照时间顺序将新来的Request加入Requestlist里面
		if (RequestList.size() == 0)
			RequestList.add(NewRequest);
		else {
			double OccurTime = 0;
			if (NewRequest.getRequesttype() == Constant.ARRIVAL)
				OccurTime = NewRequest.getArrivalTime();
			else
				OccurTime = NewRequest.getDepartTime();
			boolean inserted = false;
			for (int i = 0; i < RequestList.size(); i++) {
				Request getrequest = RequestList.get(i);
				double ComparedTime = 0;
				if (getrequest.getRequesttype() == Constant.ARRIVAL)
					ComparedTime = getrequest.getArrivalTime();
				else
					ComparedTime = getrequest.getDepartTime();
				if (OccurTime < ComparedTime) {
					RequestList.add(i, NewRequest);
					inserted = true;
					break;
				}
			}
			if (!inserted)
				RequestList.add(NewRequest);
		}
	}
}
