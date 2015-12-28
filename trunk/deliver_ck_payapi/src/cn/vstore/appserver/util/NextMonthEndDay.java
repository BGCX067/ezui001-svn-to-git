package cn.vstore.appserver.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class NextMonthEndDay {
	private Date endDay;
	private int month;
	public Date getEndDay() {
		return endDay;
	}
	public void setEndDay(Date endDay) {
		this.endDay = endDay;
	}
	public int getMonth() {
		return month;
	}
	public void setMonth(int month) {
		this.month = month;
	}
	public static NextMonthEndDay nextMonthlyEndDay(Date start,boolean isBreakToday){
		NextMonthEndDay n=new NextMonthEndDay();
		Calendar now=Calendar.getInstance();
		now.set(Calendar.HOUR_OF_DAY, 0);
		now.set(Calendar.MINUTE,0);
		now.set(Calendar.SECOND,0);
		now.set(Calendar.MILLISECOND, 0);
		Calendar s1=Calendar.getInstance();
		s1.setTime(start);
		s1.set(Calendar.HOUR_OF_DAY, 0);
		s1.set(Calendar.MINUTE,0);
		s1.set(Calendar.SECOND,0);
		s1.set(Calendar.MILLISECOND, 0);
		//如果在今天之后
		if(s1.compareTo(now)==0&&!isBreakToday){
			n.setEndDay(s1.getTime());
			n.setMonth(0);
			return n;
		}else if(s1.after(now)){
			return null;
		}else{
			//如果等于今日或小于今日,先退回前一日
			s1.add(Calendar.DAY_OF_MONTH, -1);
			int i=0;
			do{
				s1.add(Calendar.DAY_OF_MONTH, 30);
				i++;
			}while(s1.compareTo(now)<0);
			n.setEndDay(s1.getTime());
			n.setMonth(i);
			return n;
		}
	}
	public static int CompareDay(Date begin,Date end){
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		try {
		    // 将目标日期设置为毫秒形式      
		    long to;
			to = df.parse(df.format(end)).getTime();
		    // 将起始日期设置为毫秒形式      
		    long from = df.parse(df.format(begin)).getTime();      
		    // 得到相减后的毫秒，除于1000得到秒，除于60得到分钟，再除于60得到小时，除于24得到天数      
		    return (int)((to > from) ? ((to - from) / (1000 * 60 * 60 * 24)) : ((from - to) / (1000 * 60 * 60 * 24)));       	
		} catch (Throwable e) {
			return 0;
		}      
	}
	public static void main(String[] args){
		Calendar now=Calendar.getInstance();
		now.set(Calendar.YEAR, 2011);
		now.set(Calendar.MONTH,11);
		now.set(Calendar.DAY_OF_MONTH, 31);
		NextMonthEndDay n=NextMonthEndDay.nextMonthlyEndDay(now.getTime(),false);
		System.out.println(n.getMonth()+" "+n.getEndDay());
	}

}
