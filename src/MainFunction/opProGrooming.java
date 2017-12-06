package MainFunction;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import demand.Request;
import general.file_out_put;
import graphalgorithms.RouteSearching;
import network.Layer;
import network.Link;
import network.Node;
import network.NodePair;
import network.VirtualLink;
import resource.ResourceOnLink;
import subgraph.LinearRoute;
import MainFunction.FSshareOnlink;

public class opProGrooming {// ���·�ɱ���
	String OutFileName = Mymain.OutFileName;

	public boolean opprotectiongrooming(Layer iplayer, Layer oplayer, NodePair nodepair, LinearRoute route,
			int numOfTransponder, boolean flag, ArrayList<WorkandProtectRoute> wprlist,float Average) throws IOException {// flag=true��ʾ����IP�㽨���Ĺ���·��
		// flag=flase��ʾ��㽨���Ĺ���·��
		RouteSearching Dijkstra = new RouteSearching();
		Request request = new Request(nodepair);
		Node srcnode = nodepair.getSrcNode();
		Node desnode = nodepair.getDesNode();
		boolean success = false;
		double routelength = 0;
		opProGrooming opg = new opProGrooming();
		file_out_put file_io = new file_out_put();
		ArrayList<VirtualLink> provirtuallinklist = new ArrayList<>();
		ArrayList<FSshareOnlink> FSuseOnlink = new ArrayList<FSshareOnlink>();
		ArrayList<Link> opDelLink = new ArrayList<Link>();
//		System.out.println("************����·����IP�㲻��·�ɣ���Ҫ�ڹ���½�");
//		file_io.filewrite2(OutFileName, "************����·����IP�㲻��·�ɣ���Ҫ�ڹ���½�");

		// ɾ���ýڵ�ԵĹ���·�ɾ���������������·
		for (Link LinkOnRoute : route.getLinklist()) {// ȡ������·���е���·
			// System.out.println("����·����·��" + LinkOnRoute.getName());
			if (flag) {//// flag=true��ʾ���� IP�㽨���Ĺ���·��
				for (VirtualLink Vlink : LinkOnRoute.getVirtualLinkList()) {
					for (Link LinkOnPhy : Vlink.getPhysicallink()) {// ȡ��ĳһ������·�϶�Ӧ��������·

						HashMap<String, Link> oplinklist = oplayer.getLinklist();
						Iterator<String> oplinkitor = oplinklist.keySet().iterator();
						while (oplinkitor.hasNext()) {
							Link oplink = (Link) (oplinklist.get(oplinkitor.next()));
							// System.out.println("��������·������" +oplink.getName());
							if (oplink.getName().equals(LinkOnPhy.getName())) {
								if (!opDelLink.contains(oplink))
									opDelLink.add(oplink);
								break;
							}
						}
					}
				}
			} else {// flag=false��ʾ���� ��㽨���Ĺ���·��
				HashMap<String, Link> oplinklist = oplayer.getLinklist();
				Iterator<String> oplinkitor = oplinklist.keySet().iterator();
				while (oplinkitor.hasNext()) {
					Link oplink = (Link) (oplinklist.get(oplinkitor.next()));
					// System.out.println("��������·������" + oplink.getName());
					if (oplink.getName().equals(LinkOnRoute.getName())) {
						// System.out.println("ɾ���Ĺ����·�� " + oplink.getName());
						opDelLink.add(oplink);
						break;
					}
				}
			}
		}
		// ����Ϊ��һ���� ɾ����������й�����·������������·

		for (Link opdellink : opDelLink) {
			oplayer.removeLink(opdellink.getName());
		}

		Node opsrcnode = oplayer.getNodelist().get(srcnode.getName());
		Node opdesnode = oplayer.getNodelist().get(desnode.getName());

		LinearRoute opPrtectRoute = new LinearRoute(null, 0, null);
		Dijkstra.Dijkstras(opsrcnode, opdesnode, oplayer, opPrtectRoute, null);// ��iplayer������Ѱ��̱���·��

		for (Link opdellink : opDelLink) {
			oplayer.addLink(opdellink);
		} // �ָ�oplayer�����link
		opDelLink.clear();

		if (opPrtectRoute.getLinklist().size() == 0) {
//			System.out.println("����·�ɹ���޷�����");
//			file_io.filewrite2(OutFileName, "����·�ɹ���޷�����");
		} else {
//			System.out.println("����ҵ�·��:");
//			file_io.filewrite2(OutFileName, "   ");
//			file_io.filewrite2(OutFileName, "����ҵ�·��:");
			opPrtectRoute.OutputRoute_node(opPrtectRoute);
			LinearRoute route_out = new LinearRoute(null, 0, null);
//			route_out.OutputRoute_node(opPrtectRoute, OutFileName);
			int slotnum = 0;
			int IPflow = nodepair.getTrafficdemand();
			double X = 1;// 2000-4000 BPSK,1000-2000
							// QBSK,500-1000��8QAM,0-500 16QAM

			for (Link link : opPrtectRoute.getLinklist()) {
				routelength = routelength + link.getLength();
			}
			// System.out.println("����·���ĳ����ǣ�"+routelength);
			// ͨ��·���ĳ������仯���Ƹ�ʽ
			if (routelength <= 4000) {
				if (routelength > 2000 && routelength <= 4000) {
					X = 12.5;
				} else if (routelength > 1000 && routelength <= 2000) {
					X = 25.0;
				} else if (routelength > 500 && routelength <= 1000) {
					X = 37.5;
				} else if (routelength > 0 && routelength <= 500) {
					X = 50.0;
				}
				slotnum = (int) Math.ceil(IPflow / X);// ����ȡ��

				opPrtectRoute.setSlotsnum(slotnum);
//				System.out.println("����·����slot���� " + slotnum);
//				file_io.filewrite2(OutFileName, "��·����ÿ����·����slot���� " + slotnum);
				// FIX

				ArrayList<Integer> index_wave = new ArrayList<Integer>();
				index_wave = opg.FSassignOnlink(opPrtectRoute.getLinklist(), wprlist, nodepair, slotnum, oplayer);

				if (index_wave.size() == 0) {
					System.out.println("����������������·������ ��������Ƶ����Դ");
					file_io.filewrite2(OutFileName, "����������������·������  ��������Ƶ����Դ");
					WorkandProtectRoute RemoveWpr=new WorkandProtectRoute(null);
					for(WorkandProtectRoute wpr: wprlist){//��Ϊ����·���޷����� ��Ҫɾ���Ѿ�������wpr
						if(wpr.getdemand().equals(nodepair)){
							RemoveWpr=wpr;
							break;
						}
					}
					wprlist.remove(RemoveWpr);
				} else {
					success = true;
					double length = 0;
					double cost = 0;

					for (Link link : opPrtectRoute.getLinklist()) {
						ArrayList<Integer> index_wave1 = new ArrayList<Integer>();
						length = length + link.getLength();
						cost = cost + link.getCost();
						ResourceOnLink ro = new ResourceOnLink(request, link, index_wave.get(0), slotnum);
						link.setMaxslot(slotnum + link.getMaxslot());
						// System.out.println("��· " + link.getName() +
						// "�����slot�ǣ� " + link.getMaxslot()+" ����Ƶ�״�����
						// "+link.getSlotsindex().size());
						int m = index_wave.get(0);
						for (int n = 0; n < slotnum; n++) {
							index_wave1.add(m);
							// System.out.print(m);
//							file_io.filewrite_without(OutFileName, m + "  ");
							m++;
						}
						FSshareOnlink fsonLink = new FSshareOnlink(link, index_wave1);
						FSuseOnlink.add(fsonLink);
					}
//					file_io.filewrite2(OutFileName, "  ");
					String name = opsrcnode.getName() + "-" + opdesnode.getName();
					int index = iplayer.getLinklist().size();// ��Ϊiplayer�����link��һ��һ������ȥ��
																// ����������index
					Link finlink = iplayer.findLink(srcnode, desnode);
					Link createlink = new Link(null, 0, null, iplayer, null, null, 0, 0);
					boolean findflag = false;
					try {
						System.out.println("IP�����ҵ���·" + finlink.getName());
						file_io.filewrite2(OutFileName, "IP�����ҵ���·" + finlink.getName());
						findflag = true;
					} catch (java.lang.NullPointerException ex) {
						System.out.println("IP ��û�и���·��Ҫ�½���·");
						file_io.filewrite2(OutFileName, "IP ��û�и���·��Ҫ�½���·");
						createlink = new Link(name, index, null, iplayer, srcnode, desnode, length, cost);
						iplayer.addLink(createlink);
					}

					VirtualLink Vlink = new VirtualLink(srcnode.getName(), desnode.getName(), 1, 0);
					Vlink.setnature(1);
					Vlink.setlength(length);
					Vlink.setcost(cost);
					Vlink.setUsedcapacity(Vlink.getUsedcapacity() + nodepair.getTrafficdemand());
					Vlink.setFullcapacity(slotnum * X);// �������flow�Ǵ����������
					Vlink.setRestcapacity(Vlink.getFullcapacity() - Vlink.getUsedcapacity());
					Vlink.setPhysicallink(opPrtectRoute.getLinklist());
					provirtuallinklist.add(Vlink);
					// numOfTransponder = numOfTransponder + 2;

					if (findflag) {// �����IP�����Ѿ��ҵ�����·
						// System.out.println("������·������" +
						// finlink.getVirtualLinkList().size());
//						file_io.filewrite2(OutFileName, "������·������" + finlink.getVirtualLinkList().size());
						finlink.getVirtualLinkList().add(Vlink);
//						System.out.println("IP���Ѵ��ڵ���· " + finlink.getName() + " �����µı���������· ���������flow: "
//								+ Vlink.getUsedcapacity() + "\n " + "���е�flow:  " + Vlink.getFullcapacity()
//								+ "    Ԥ����flow��  " + Vlink.getRestcapacity() + "\n" + "������·���ȣ�" + Vlink.getlength()
//								+ "   " + "������·cost�� " + Vlink.getcost());
//						file_io.filewrite2(OutFileName,
//								"IP���Ѵ��ڵ���· " + finlink.getName() + " �����µı���������· ���������flow: " + Vlink.getUsedcapacity()
//										+ "\n " + "���е�flow:  " + Vlink.getFullcapacity() + "    Ԥ����flow��  "
//										+ Vlink.getRestcapacity() + "\n" + "������·���ȣ�" + Vlink.getlength() + "   "
//										+ "������·cost�� " + Vlink.getcost());
//						System.out.println("*********�Ѵ���IP����·��  " + finlink.getName() + "  �ϵ�������·������ "
//								+ finlink.getVirtualLinkList().size());
//						file_io.filewrite2(OutFileName, "*********�Ѵ���IP����·��  " + finlink.getName() + "  �ϵ�������·������ "
//								+ finlink.getVirtualLinkList().size());
					} else {
//						System.out.println("������·������" + createlink.getVirtualLinkList().size());
//						file_io.filewrite2(OutFileName, "������·������" + createlink.getVirtualLinkList().size());
						createlink.getVirtualLinkList().add(Vlink);
//						System.out.println("IP�����½���· " + createlink.getName() + " �����µı���������· ���������flow: "
//								+ Vlink.getUsedcapacity() + "\n " + "���е�flow:  " + Vlink.getFullcapacity()
//								+ "    Ԥ����flow��  " + Vlink.getRestcapacity() + "\n" + "������·���ȣ�" + Vlink.getlength()
//								+ "   " + "������·cost�� " + Vlink.getcost());
//						file_io.filewrite2(OutFileName,
//								"IP�����½���· " + createlink.getName() + " �����µı���������· ���������flow: " + Vlink.getUsedcapacity()
//										+ "\n " + "���е�flow:  " + Vlink.getFullcapacity() + "    Ԥ����flow��  "
//										+ Vlink.getRestcapacity() + "\n" + "������·���ȣ�" + Vlink.getlength() + "   "
//										+ "������·cost�� " + Vlink.getcost());
//						System.out.println("*********�½�IP��·��  " + createlink.getName() + "  �ϵ�������·������ "
//								+ createlink.getVirtualLinkList().size());
//						file_io.filewrite2(OutFileName, "*********�½�IP��·��  " + createlink.getName() + "  �ϵ�������·������ "
//								+ createlink.getVirtualLinkList().size());
					}
				}
			}
			if (routelength > 4000) {
				ProregeneratorPlace rgp = new ProregeneratorPlace();
				success = rgp.ProRegeneratorPlace(nodepair, opPrtectRoute, wprlist, routelength, oplayer, iplayer,IPflow, request,Average);
			}
		}

		if (success && routelength < 4000) {
			for (WorkandProtectRoute wpr : wprlist) {
				if (wpr.getdemand().equals(nodepair)) {
					wpr.setproroute(opPrtectRoute);
					ArrayList<Link> totallink = new ArrayList<>();
					totallink = opPrtectRoute.getLinklist();
					wpr.setrequest(request);
					wpr.setprolinklist(totallink);
					wpr.setFSoneachLink(FSuseOnlink);
					wpr.setprovirtuallinklist(provirtuallinklist);
					wpr.setregthinglist(null);
				}
			}
		}
		return success;
	}

	public ArrayList<Integer> FSassignOnlink(ArrayList<Link> linklist, ArrayList<WorkandProtectRoute> wprlist,
			NodePair nodePair, int slotnum, Layer oplayer) {
		// ������ʵ���ڸ���һ������·�ɵ���·����ʱ��ͨ���ж��ܷ���֮ǰ�����ı���·������ Ȼ��������Ƶ�� ʵ����󻯹���FS
		file_out_put file_io = new file_out_put();
		Test t = new Test();
		ArrayList<Integer> RemoveslotIndex = new ArrayList<>();
		HashMap<WorkandProtectRoute, ArrayList<Integer>> shareslotWPR = new HashMap<WorkandProtectRoute, ArrayList<Integer>>();
		HashMap<WorkandProtectRoute, ArrayList<Integer>> NoShareWPR = new HashMap<WorkandProtectRoute, ArrayList<Integer>>();
		WorkandProtectRoute nowwpr = new WorkandProtectRoute(null);
		ArrayList<FSshareOnlink> fsonLinklist = new ArrayList<>();

		for (WorkandProtectRoute wpr : wprlist) {
			if (wpr.getdemand().equals(nodePair)) {
				nowwpr = wpr;
			}
		}

		for (Link link : linklist) {
			ArrayList<Integer> shareslotIndex = new ArrayList<>();
			ArrayList<Integer> NoShareslotIndex = new ArrayList<>();
			shareslotWPR.clear();
			for (WorkandProtectRoute wpr : wprlist) {
				if (wpr.getdemand().equals(nodePair))
					continue;
				// System.out.println("��ʱ�Ľڵ��Ϊ "+wpr.getdemand().getName()+"
				// ��ʱ��link Ϊ"+ link.getName());
				// file_io.filewrite2(OutFileName,"��ʱ�Ľڵ��Ϊ
				// "+wpr.getdemand().getName()+" ��ʱ��link Ϊ"+ link.getName());
				if (wpr.getprolinklist().contains(link)) {
					int cross = t.linklistcompare(nowwpr.getworklinklist(), wpr.getworklinklist());
					if (cross == 0) {// ��ʾ����·�����FS���Թ���
						ArrayList<FSshareOnlink> FSShareOnlink = wpr.getFSoneachLink();
						// file_io.filewrite2(OutFileName,"��ʱ��WPR Ϊ
						// "+wpr.getdemand().getName());
						if (FSShareOnlink != null) {
							for (FSshareOnlink FSOnoneLink : FSShareOnlink) {
								if (FSOnoneLink.getlink().equals(link)) {
									for (int share : FSOnoneLink.getslotIndex()) {
										if (!shareslotIndex.contains(share))
											// System.out.println("���Թ�����FS Ϊ
											// "+share);
											// file_io.filewrite2(OutFileName,"���Թ�����FS
											// Ϊ "+share);
											shareslotIndex.add(share);
									}
								}
							}
						}
					}
					if (cross == 1) {// ��ʾ����·�����FS�����Թ���
						ArrayList<FSshareOnlink> FSShareOnlink = wpr.getFSoneachLink();
						if (FSShareOnlink != null) {
							for (FSshareOnlink FSOnoneLink : FSShareOnlink) {
								if (FSOnoneLink.getlink().equals(link)) {
									for (int NOshare : FSOnoneLink.getslotIndex()) {
										if (!NoShareslotIndex.contains(NOshare)) {
											// System.out.println("�����Թ�����FS Ϊ
											// "+NOshare);
											// file_io.filewrite2(OutFileName,"�����Թ�����FS
											// Ϊ "+NOshare);
											NoShareslotIndex.add(NOshare);
										}
									}
								}
							}

						}
					}

					if (shareslotIndex.size() != 0)
						shareslotWPR.put(wpr, shareslotIndex);
					if (NoShareslotIndex.size() != 0)
						NoShareWPR.put(wpr, NoShareslotIndex);
				}
			} // ��ÿһ��link����Ŀɹ����Ͳ��ɹ���FS����ͳ�Ʊ���
			// file_io.filewrite2(OutFileName,"");
			// file_io.filewrite2(OutFileName,"FS�Ƴ�����");
			for (WorkandProtectRoute wpr : wprlist) {
				RemoveslotIndex.clear();
				if (shareslotWPR.keySet().contains(wpr)) {

					for (int re : shareslotWPR.get(wpr)) {// ȡ�����Թ�����FS
						// file_io.filewrite2(OutFileName," ");
						// file_io.filewrite2(OutFileName,"���Թ�����FS "+re);
						for (WorkandProtectRoute comwpr : wprlist) {
							// file_io.filewrite2(OutFileName,"�����Ƚϵ�WPR
							// "+comwpr.getdemand().getName());
							if (NoShareWPR.keySet().contains(comwpr)) {
								if (NoShareWPR.get(comwpr).contains(re)) {// ˵����FS������ҵ�����ǲ����Թ�����
									// file_io.filewrite2(OutFileName,"��FS�����Թ���
									// "+re);
									if (!RemoveslotIndex.contains(re)) {
										RemoveslotIndex.add(re);
										break;
									}
								}
							}
						}
					}
					// test

					// for (int remove : RemoveslotIndex) {
					// file_io.filewrite_without(OutFileName,"��Ҫ�Ƴ���FSΪ"+remove+" ");
					// }
					// file_io.filewrite2(OutFileName,"");
					// for(int share:shareslotWPR.get(wpr)){
					// file_io.filewrite_without(OutFileName,"���Թ�����FSΪ "+share+"
					// ");
					// }
					// file_io.filewrite2(OutFileName,"");
					if (RemoveslotIndex.size() != 0 && RemoveslotIndex != null) {
						for (int remove : RemoveslotIndex) {

							// file_io.filewrite2(OutFileName,"���Թ�����FS��Ŀ��ʣ
							// "+shareslotWPR.get(wpr).size());
							// for(int last:shareslotWPR.get(wpr)){
							// file_io.filewrite_without(OutFileName,last+" ");
							// }
							// file_io.filewrite2(OutFileName,"");
							int index = shareslotWPR.get(wpr).indexOf(remove);
							// file_io.filewrite2(OutFileName," "+remove);
							// file_io.filewrite2(OutFileName,"��Ҫ�Ƴ�����·indexΪ
							// "+index);
							shareslotWPR.get(wpr).remove(index);
							// file_io.filewrite2(OutFileName,"�Ѿ��Ƴ���FSΪ
							// "+remove);
						} // ��ÿ��WPR���治���Թ�����FSȥ��
					}
					FSshareOnlink fsol = new FSshareOnlink(link, shareslotIndex);
					fsol.setwpr(wpr);
					fsonLinklist.add(fsol);
				}
			}
			
			for (WorkandProtectRoute wpr : wprlist) {
				if (shareslotWPR.keySet().contains(wpr)) {
					if (shareslotWPR.get(wpr).size() != 0) {
//						file_io.filewrite2(OutFileName, " ");
//						file_io.filewrite_without(OutFileName, "��· " + link.getName() + " �Ͽ��Թ�����slotΪ ");
//						System.out.print("��· " + link.getName() + " �Ͽ��Թ�����slotΪ ");
						for (int release : shareslotWPR.get(wpr)) {// �ͷſɹ�����Դ
							Request request = wpr.getrequest();

							// System.out.println("�ɹ�����·��ҵ��Ϊ"+request.getNodepair().getName()+"
							// �ɹ�������·Ϊ��"+link.getName()+" ��·�ϵ�FSΪ��"+release);//test);
							// file_io.filewrite2(OutFileName,"�ɹ�����·��ҵ��Ϊ "+request.getNodepair().getName()+"
							// �ɹ�������·Ϊ��"+link.getName()+" ��·�ϵ�FSΪ��"+release);//test
							// test

							// System.out.println("����·�ϵ�request����"+link.getSlotsarray().get(release).getoccupiedreqlist().size());
							// file_io.filewrite2(OutFileName,"����·�ϵ�request����
							// "+link.getSlotsarray().get(release).getoccupiedreqlist().size());
							// for(Request
							// re:link.getSlotsarray().get(release).getoccupiedreqlist()){
							// System.out.println("ռ�ø���·��FS�Ľڵ��Ϊ
							// "+re.getNodepair().getName());
							// file_io.filewrite2(OutFileName,"ռ�ø���·��FS�Ľڵ��Ϊ
							// "+re.getNodepair().getName());
							// }

							
							// link.getSlotsarray().get(release).getoccupiedreqlist().get(0);
							// link.getSlotsarray().get(release).getoccupiedreqlist().remove(res);
							link.getSlotsarray().get(release).getoccupiedreqlist().remove(request);
							// test
//							file_io.filewrite_without(OutFileName, release + "    ");
//							System.out.print(release + "    ");
						}
//						file_io.filewrite2(OutFileName, " ");
						}
				}
			}
		
		} // ÿһ��link�����FS���ͷ����
//		file_io.filewrite2(OutFileName, "    ");

		// link������Թ�������Դ�ͷ���� ֮�����RSA
		ArrayList<Integer> index_wave = new ArrayList<Integer>();
		Mymain mm = new Mymain();
		// file_io.filewrite2(OutFileName,"ÿ����·����Ҫ��FS��Ϊ�� "+slotnum );
		index_wave = mm.spectrumallocationOneRoute(false, null, linklist, slotnum); // ÿ��link�����ռ����ô��
		if(index_wave!=null&& index_wave.size()!=0){
//		file_io.filewrite2(OutFileName, "�˴�RSA�����slot���Ϊ " + index_wave.get(0) + " ,����Ϊ " + slotnum);
//		System.out.println("�˴�RSA�����slot���Ϊ " + index_wave.get(0) + " ,����Ϊ " + slotnum);
			int share = 0, newFS = 0;
			for (Link link : linklist) {// �ָ�֮ǰռ�õ�
				for (FSshareOnlink fl : fsonLinklist) {// ����ÿһ��linkҪ����֮ǰ���е�ҵ��
					if (fl.getlink().equals(link)) {
						Request request = fl.getwpr().getrequest();
						for (int recovery : fl.getslotIndex()) {
							link.getSlotsarray().get(recovery).getoccupiedreqlist().add(request);
							
							for (int co = index_wave.get(0); co < index_wave.get(0) + slotnum; co++) {
								if (co == recovery) {
									share++;
									break;
								}
							}
						}
					}
				}
				if (slotnum < share) {
					share = slotnum;
				}
				newFS = newFS + slotnum - share;
				
			}
			nodePair.setSlotsnum(newFS);
			
		}
//		else{
//			System.out.println("·������ ��������Ƶ����Դ");
//			file_io.filewrite2(OutFileName, "·������ ��������Ƶ����Դ");
//		}
//		file_io.filewrite2(OutFileName, "�˴�RSA��Ҫ����slot��Ϊ " + newFS);
		// file_io.filewrite2(OutFileName,"");
		// file_io.filewrite2(OutFileName,"�ָ�ռ��֮��");
		// for(Link link:linklist){
		// for(int n=0;n<link.getSlotsarray().size();n++){
		// if (link.getSlotsarray().get(n).getoccupiedreqlist().size() !=
		// 0){//˵����FS��ռ��
		// System.out.println("��·"+link.getName()+"��FS "+n+" �ѱ�
		// "+link.getSlotsarray().get(n).getoccupiedreqlist().size()+" ��ҵ��ռ��");
		// file_io.filewrite2(OutFileName,"��·"+link.getName()+"��FS "+n+" �ѱ�
		// "+link.getSlotsarray().get(n).getoccupiedreqlist().size()+" ��ҵ��ռ��");

		// for(Request re:link.getSlotsarray().get(n).getoccupiedreqlist()){
		// if(re!=null){
		// System.out.println("��·"+link.getName()+"��FS "+n+"
		// �ѱ�ҵ��ռ��"+re.getNodepair().getName());
		// file_io.filewrite2(OutFileName,"��·"+link.getName()+"��FS "+n+" �ѱ�ҵ��
		// "+re.getNodepair().getName()+"ռ��");
		// }
		// }
		// }
		// }
		// }

		
		return index_wave;

	}
}