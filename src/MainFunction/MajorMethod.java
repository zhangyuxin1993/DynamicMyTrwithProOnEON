package MainFunction;

import java.io.IOException;
import java.util.ArrayList;

import network.Layer;
import network.NodePair;
import subgraph.LinearRoute;

public class MajorMethod {
	// ����IP��·�ɹ���
	public boolean majormethod(NodePair nodepair, Layer iplayer,Layer oplayer,float Average) throws IOException {
		boolean ipWorkFlag = false, ipproFlag = false,opworkFlag = false, 
				opProFlag=false,FinalSuccess=false;
		LinearRoute ipWorkRoute = new LinearRoute(null, 0, null);
		LinearRoute opWorkRoute = new LinearRoute(null, 0, null);
		int numOfTransponder=0;
		ArrayList<WorkandProtectRoute> wprlist = new ArrayList<>();
		
		IPWorkingGrooming ipwg = new IPWorkingGrooming();
		ipWorkFlag = ipwg.ipWorkingGrooming(nodepair, iplayer, oplayer, numOfTransponder, ipWorkRoute, wprlist);// ��ip�㹤��·��
		if (ipWorkFlag) {// ip�㹤��·�ɳɹ� ��������
			ipProGrooming ipprog = new ipProGrooming();
			ipproFlag = ipprog.ipprotectiongrooming(iplayer, oplayer, nodepair, ipWorkRoute, numOfTransponder, true,wprlist);
			
			if (!ipproFlag) {// ��ip�㱣��·������ ���ڹ��·�ɱ���
				opProGrooming opg = new opProGrooming();
				opProFlag=opg.opprotectiongrooming(iplayer, oplayer, nodepair, ipWorkRoute, numOfTransponder, true, wprlist,Average);
			}
		}
		if(ipWorkFlag&&(ipproFlag||opProFlag)) FinalSuccess=true;
	
		// ip�㹤��·�ɲ��ɹ� �ڹ��·�ɹ���
		if (!ipWorkFlag) {
			opWorkingGrooming opwg = new opWorkingGrooming();
			opworkFlag = opwg.opWorkingGrooming(nodepair, iplayer, oplayer, opWorkRoute, wprlist,Average);
			if (opworkFlag) {// �ڹ��ɹ���������·����������·��
				ipProGrooming ipprog = new ipProGrooming();
				ipproFlag = ipprog.ipprotectiongrooming(iplayer, oplayer, nodepair, opWorkRoute, numOfTransponder,false, wprlist);
				if (!ipproFlag) {// ��ip�㱣��·������ ���ڹ��·�ɱ���
					opProGrooming opg = new opProGrooming();
					opProFlag=opg.opprotectiongrooming(iplayer, oplayer, nodepair, opWorkRoute, numOfTransponder, false,wprlist,Average);
				}
				
			}
		} 
		if(opworkFlag&&(ipproFlag||opProFlag)) FinalSuccess=true;
		return FinalSuccess;
	}
}
