package MainFunction;

public class MajorMethod {
	// ����IP��·�ɹ���
	 
	 
	IPWorkingGrooming ipwg = new IPWorkingGrooming();
	iproutingFlag = ipwg.ipWorkingGrooming(nodepair, iplayer, oplayer, numOfTransponder, ipWorkRoute, wprlist);// ��ip�㹤��·��
	if (iproutingFlag) {// ip�㹤��·�ɳɹ� ��������
		ipProGrooming ipprog = new ipProGrooming();
		ipproFlag = ipprog.ipprotectiongrooming(iplayer, oplayer, nodepair, ipWorkRoute, numOfTransponder, true,wprlist);
		
		if (!ipproFlag) {// ��ip�㱣��·������ ���ڹ��·�ɱ���
			opProGrooming opg = new opProGrooming();
			opg.opprotectiongrooming(iplayer, oplayer, nodepair, ipWorkRoute, numOfTransponder, true, wprlist,Average);
		}
	}

	// ip�㹤��·�ɲ��ɹ� �ڹ��·�ɹ���
	if (!iproutingFlag) {
		opWorkingGrooming opwg = new opWorkingGrooming();
		opworkFlag = opwg.opWorkingGrooming(nodepair, iplayer, oplayer, opWorkRoute, wprlist,Average);
		if (opworkFlag) {// �ڹ��ɹ���������·����������·��
			ipProGrooming ipprog = new ipProGrooming();
			ipproFlag = ipprog.ipprotectiongrooming(iplayer, oplayer, nodepair, opWorkRoute, numOfTransponder,
					false, wprlist);
			if (!ipproFlag) {// ��ip�㱣��·������ ���ڹ��·�ɱ���
				opProGrooming opg = new opProGrooming();
				opg.opprotectiongrooming(iplayer, oplayer, nodepair, opWorkRoute, numOfTransponder, false,wprlist,Average);
			}
		 
		}
	} 

}
