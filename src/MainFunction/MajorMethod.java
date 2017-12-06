package MainFunction;

public class MajorMethod {
	// 先在IP层路由工作
	 
	 
	IPWorkingGrooming ipwg = new IPWorkingGrooming();
	iproutingFlag = ipwg.ipWorkingGrooming(nodepair, iplayer, oplayer, numOfTransponder, ipWorkRoute, wprlist);// 在ip层工作路由
	if (iproutingFlag) {// ip层工作路由成功 建立保护
		ipProGrooming ipprog = new ipProGrooming();
		ipproFlag = ipprog.ipprotectiongrooming(iplayer, oplayer, nodepair, ipWorkRoute, numOfTransponder, true,wprlist);
		
		if (!ipproFlag) {// 在ip层保护路由受阻 则在光层路由保护
			opProGrooming opg = new opProGrooming();
			opg.opprotectiongrooming(iplayer, oplayer, nodepair, ipWorkRoute, numOfTransponder, true, wprlist,Average);
		}
	}

	// ip层工作路由不成功 在光层路由工作
	if (!iproutingFlag) {
		opWorkingGrooming opwg = new opWorkingGrooming();
		opworkFlag = opwg.opWorkingGrooming(nodepair, iplayer, oplayer, opWorkRoute, wprlist,Average);
		if (opworkFlag) {// 在光层成功建立工作路径后建立保护路径
			ipProGrooming ipprog = new ipProGrooming();
			ipproFlag = ipprog.ipprotectiongrooming(iplayer, oplayer, nodepair, opWorkRoute, numOfTransponder,
					false, wprlist);
			if (!ipproFlag) {// 在ip层保护路由受阻 则在光层路由保护
				opProGrooming opg = new opProGrooming();
				opg.opprotectiongrooming(iplayer, oplayer, nodepair, opWorkRoute, numOfTransponder, false,wprlist,Average);
			}
		 
		}
	} 

}
