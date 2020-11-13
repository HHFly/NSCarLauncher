package com.nushine.nshlbus.com.driverlayer.os_driverApp;

public class Temperature{
	public int Cur_Temp;
	final int Max_Temp = 100;//最高温度
	final int Min_Temp = -30;//最低温度
	final int Ref_vol = 5000;	//基准电压5V
	final int AD_ACC = 4096;	//12位精度
	final int Ref_Res=100000;	//分压电阻
	final int[] afe_temp_table = {
			6550,6751,6959,7175,7398,7630,7871,8121,8380,8648,
			8927,9217,9518,9830,10154,10490,10835,11192,11563,11947,
			12346,12760,13190,13636,14099,14581,15081,15600,16140,16702,
			17285,17892,18524,19181,19865,20576,21317,22089,22893,23730,
			24603,25513,26461,27450,28482,29559,30683,31856,33080,34360,
			35696,37092,38552,40078,41674,43343,45089,46917,48830,50833,
			52931,55128,57431,59845,62375,65028,67811,70731,73795,77011,
			80387,83932,87656,91568,95679,100000,104542,109318,114341,119624,
			125182,131031,137186,143665,150486,157667,165229,173193,181581,190416,
			199723,209528,219858,230741,242206,254286,267012,280419,294541,309415,
			325079,342152,360209,379308,399513,420892,443517,467467,492824,519681,
			548133,577921,609577,643236,679045,717160,757752,801002,847109,896283,
			948752,1004214,1063407,1126648,1194291,1266731,1344410,1427827,1517543,1614191};

	public Temperature(){
		Cur_Temp = 0;
	}

	public void CalculationTemp(float sample){
		float samp_res=0;
		sample = Ref_vol*(sample/AD_ACC);
		if(sample>=Ref_vol){
			Cur_Temp = Min_Temp;
		}else{
			samp_res = (sample*Ref_Res)/(5000-sample);
			for(int i=0;i<afe_temp_table.length;i++){
				if(samp_res<=afe_temp_table[i]){
					Cur_Temp = Max_Temp - i;
					break;
				}
			}
		}
	}
}