package general;

import subgraph.LinearRoute;
public class Time {
	String name;//��¼��Ӧ��nodepair������
    double time;//���ʱ��
    int FlagforSetorRelease;//���������ͷű�־
    LinearRoute linearroute;   
    int route_index;
    int Occpy_S=Constant.maxium;
  
public Time(String name,double time,int flagforsetorrelease, LinearRoute linearroute){
   this.name=name;
   this.time=time;
   this.FlagforSetorRelease=flagforsetorrelease;
   this.linearroute=linearroute;      
	}
//��¼Ƶ��ռ��
public int getOccpy_S() {
	return Occpy_S;
}
public void setOccpy_S(int occpy_s) {
	Occpy_S = occpy_s;
}

public int getRoute_index() {
		return route_index;
	}

	public void setRoute_index(int route_index) {
		this.route_index = route_index;
	}

public LinearRoute getRoute() {
	return linearroute;
}

public void setRoute(LinearRoute linearroute) {
	this.linearroute = linearroute;
}

public int getKeytime() {
	return FlagforSetorRelease;
}
public void setKeytime(int keytime) {
	this.FlagforSetorRelease = keytime;
}

public String getName() {
	return name;
}
public void setName(String name) {
	this.name = name;
}
public double getTime() {
	return time;
}
public void setTime(double time) {
	this.time = time;
}

}
