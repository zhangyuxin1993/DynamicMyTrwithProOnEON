package MainFunction;

import java.io.IOException;
import java.util.ArrayList;

import network.Layer;
import network.NodePair;
import subgraph.LinearRoute;

public class MajorMethod {
	// 先在IP层路由工作
	public boolean majormethod(NodePair nodepair, Layer iplayer,Layer oplayer,float Average) throws IOException {
		boolean ipWorkFlag = false, ipproFlag = false,opworkFlag = false, 
				opProFlag=false,FinalSuccess=false;
		LinearRoute ipWorkRoute = new LinearRoute(null, 0, null);
		LinearRoute opWorkRoute = new LinearRoute(null, 0, null);
		int numOfTransponder=0;
		ArrayList<WorkandProtectRoute> wprlist = new ArrayList<>();
		
		IPWorkingGrooming ipwg = new IPWorkingGrooming();
		ipWorkFlag = ipwg.ipWorkingGrooming(nodepair, iplayer, oplayer, numOfTransponder, ipWorkRoute, wprlist);// 在ip层工作路由
		if (ipWorkFlag) {// ip层工作路由成功 建立保护
			ipProGrooming ipprog = new ipProGrooming();
			ipproFlag = ipprog.ipprotectiongrooming(iplayer, oplayer, nodepair, ipWorkRoute, numOfTransponder, true,wprlist);
			
			if (!ipproFlag) {// 在ip层保护路由受阻 则在光层路由保护
				opProGrooming opg = new opProGrooming();
				opProFlag=opg.opprotectiongrooming(iplayer, oplayer, nodepair, ipWorkRoute, numOfTransponder, true, wprlist,Average);
			}
		}
		if(ipWorkFlag&&(ipproFlag||opProFlag)) FinalSuccess=true;
	
		// ip层工作路由不成功 在光层路由工作
		if (!ipWorkFlag) {
			opWorkingGrooming opwg = new opWorkingGrooming();
			opworkFlag = opwg.opWorkingGrooming(nodepair, iplayer, oplayer, opWorkRoute, wprlist,Average);
			if (opworkFlag) {// 在光层成功建立工作路径后建立保护路径
				ipProGrooming ipprog = new ipProGrooming();
				ipproFlag = ipprog.ipprotectiongrooming(iplayer, oplayer, nodepair, opWorkRoute, numOfTransponder,false, wprlist);
				if (!ipproFlag) {// 在ip层保护路由受阻 则在光层路由保护
					opProGrooming opg = new opProGrooming();
					opProFlag=opg.opprotectiongrooming(iplayer, oplayer, nodepair, opWorkRoute, numOfTransponder, false,wprlist,Average);
				}
				
			}
		} 
		if(opworkFlag&&(ipproFlag||opProFlag)) FinalSuccess=true;
		return FinalSuccess;
	}
}
