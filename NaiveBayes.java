import java.sql.*;
import java.util.*;

class NaiveBayes
{
    public String execute(String bp,String cholestrol,String fhistory,String bmi,String age){
    {
	String result = "";	
        try
		{
			/*
			c1 and c2 denote class 1 & class 2. 
			class1 :- Patient has a heart disease
			class2 :- Patient doesn't have a heart disease
			*/
			Class.forName("com.mysql.jdbc.Driver");
			Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/heart","root","root");
			Statement s = con.createStatement();
			String query = null;
			ResultSet rs = null;
			int c1=0 ,c2=0 ,n=0;
                        

			query ="SELECT     COUNT(*) AS Expr1    FROM   heart WHERE (Class = '1') ";
			s.execute(query);
			rs= s.getResultSet();
			if(rs.next())
					//Count of cases when Patient has a heart disease in training set
					c1=Integer.parseInt(rs.getString(1));

			query ="SELECT     COUNT(*) AS Expr1    FROM   heart WHERE (class = '0') ";
			s.execute(query);
			rs= s.getResultSet();
			if(rs.next())
					//Count of cases when Patient doesn't have a heart disease in training set
					c2=Integer.parseInt(rs.getString(1)); 

			query = "SELECT     COUNT(*) AS Expr1   FROM  heart";
			s.execute(query);
			rs= s.getResultSet();
			if(rs.next())
					//Count of total cases in training set
					n = Integer.parseInt(rs.getString(1)); 

			float pc1 = (float)c1/n; //General probability for class c1
			float pc2 = (float)c2/n; //General probability for class c2

			System.out.println("c1= " +c1 +"\nc2="+c2+"\ntotal="+n);
			System.out.println("p(c1)="+pc1);
			System.out.println("p(c2)="+pc2); 

			//Scanner sc = new Scanner(System.in);

			
			
			// Accept the parameter values for which class is to be predicted
			
			//System.out.println("Enter Systolic_Blood_Pressure:");
			/*bp = sc.next();

			System.out.println("Enter Cholestrol Level:");
			cholestrol = sc.next();

			System.out.println("Enter whether patient has a family history regarding heart disease: (1/0)");
			fhistory = sc.next();

			System.out.println("Enter BMI:");
			bmi = sc.next();

                        System.out.println("Enter Age:");
			age = sc.next();*/
                        
			float pinc1=0,pinc2=0;
			//pinc1 = probability of prediction to be class1 (has heart disease)
			//pinc2 = probability of prediction to be class2  (doesn't have a heart disease)
			NaiveBayes obj = new NaiveBayes();
			pinc1 = obj.pfind(bp,cholestrol,fhistory,bmi,age,"1");
			pinc2 = obj.pfind(bp,cholestrol,fhistory,bmi,age,"0");

			pinc1 = pinc1 * pc1;
			pinc2 = pinc2 * pc2;
                        

			// compare pinc1 & pinc2 and predict the class that Patient has a heart disease or not
			if(pinc1 > pinc2){
				System.out.println("Patient has a heart disease");
                                result = "Patient has a heart disease";
                        }else{
				System.out.println("Patient doesn't have a heart disease");
                                result = "Patient doesnt have a heart disease";
                        }
                        String query1="insert into userinfo values ('"+bp+"','"+cholestrol+"','"+fhistory+"','"+bmi+"','"+age+"','"+result+"')";
                        s.execute(query1);
                        
			s.close();
			con.close();
		}
		catch(Exception e)
		{
			System.out.println("Exception:"+ e);
		}
                
                return result;
	}
    }

	public float pfind(String bp,String cholestrol,String fhistory,String bmi,String age,String class1)
	{
                boolean flags[] = new boolean[5];
		float ans = 0;
		try{
			Scanner sc = new Scanner(System.in);
			Class.forName("com.mysql.jdbc.Driver");
			Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/heart","root","root");
			Statement s = con.createStatement();
			String query = null;
			ResultSet rs = null;
			int alpha[] = new int [5];
                        int total=0;
			
			/* 	Queries below are constructed using parameter values of bp,cholestrol,fhistory,bmi,age,class
				passed to function. Function finds probability for every individual parameter of provided class value 
				and using naive baye's theorem it calculates total probability */
			

			query ="SELECT    COUNT(*) AS Expr1     FROM   heart WHERE   (Systolic_Blood_Pressure = '"+ bp + "' ) AND (class = '" +class1 +"') ";
			s.execute(query);
			rs= s.getResultSet();
			if(rs.next())
                            alpha[0]=Integer.parseInt(rs.getString(1));
			// a = count of values in training set having bp , class same as passed in argument
                        
			query ="SELECT    COUNT(*) AS Expr1     FROM   heart WHERE   ( Cholestrol = '"+ cholestrol + "' ) AND (class = '" +class1 +"') ";
			s.execute(query);
			rs= s.getResultSet();
			if(rs.next())
					alpha[1]=Integer.parseInt(rs.getString(1));
			// b = count of values in training set having cholestrol , class same as passed in argument


			query ="SELECT    COUNT(*) AS Expr1     FROM   heart WHERE   ( Family_History = '"+ fhistory + "' ) AND (class = '" +class1 +"') ";
			s.execute(query);
			rs= s.getResultSet();
			if(rs.next())
					alpha[2]=Integer.parseInt(rs.getString(1));
			// c = count of values in training set having fhistory , class same as passed in argument


			query ="SELECT    COUNT(*) AS Expr1     FROM   heart WHERE   ( BMI= '"+ bmi + "' ) AND (class = '" +class1 +"')";
			s.execute(query);
			rs= s.getResultSet();
			if(rs.next())
					alpha[3]=Integer.parseInt(rs.getString(1)); 
			// d = count of values in training set having bmi , class same as passed in argument
                        
                        query ="SELECT    COUNT(*) AS Expr1     FROM   heart WHERE   ( Age= '"+ age + "' ) AND (class = '" +class1 +"')";
			s.execute(query);
			rs= s.getResultSet();
			if(rs.next())
					alpha[4]=Integer.parseInt(rs.getString(1)); 
			// e = count of values in training set having age , class same as passed in argument
			query ="SELECT    COUNT(*) AS Expr1     FROM   heart WHERE   (class = '" +class1 +"') ";
			s.execute(query);
			rs= s.getResultSet();
                        if(alpha[0]>0)
                            flags[0] = true;
                        else if(alpha[1]>0)
                            flags[1] = true;
                        else if(alpha[2]>0)
                            flags[2] = true;
                        else if(alpha[3]>0)
                            flags[3] = true;
                        else if(alpha[4]>0)
                            flags[4] = true;
                        int flagcount = 0;
                        for(int i=0;i<5;i++)
                        {
                            if(flags[i]==false)
                                flagcount++;
                        }
                        int agefactor=Integer.parseInt(age);
                        float cholestrolfactor = Float.valueOf(cholestrol);
                        if((flagcount >=2)&&(agefactor>=40)&&( cholestrolfactor>=4.7))
                        {
                            for(int i=0;i<5;i++)
                            {
                                if(flags[i]==false)
                                    alpha[i]=1;
                            }
                        }
                        /*for(int i =0 ; i<5;i++)
                        System.out.println("rubal--> "+i+" "+alpha[i]);*/
			if(rs.next())
					total=Integer.parseInt(rs.getString(1)); //total no resuults
			ans = (float)alpha[0] / (float)total  * (float)alpha[1] /(float)total * (float)alpha[2] /(float)total * (float)alpha[3] /(float)total * (float)alpha[4] / (float)total ;
			//calculating total probability by naive bayes
			
			s.close();
			con.close();
		}
		catch(Exception e)
		{
			System.out.println("Exception:"+ e);
		}
		return ans;
	}
}