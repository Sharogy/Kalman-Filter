package util;

import org.apache.commons.math3.distribution.NormalDistribution;

public class kalmanfilter {
	
	private double kfUWBvalue;
	private double kfOFvalue;
	private double kfIMUvalue;
	private double KFsample;
	
	private int xflow;
	private int yflow;
	
	//input value
//	private double UWBXinput;
//	private double UWBYinput;
//	private double OFXinput;
//	private double OFYinput;
//	private double IMUXinput;
//	private double IMUYinput;
		
	private static double[][] x_prev = {{0},{0},{0},{0},{0},{0}};
	private static double[][] p_prev; 
	
	
	public kalmanfilter (double kFUWBvalue_internal, double kfOFvalue_internal, double kFIMUvalue_internal, double KFcovar_internal, double KFsample_internal)
	{
		this.kfUWBvalue = kFUWBvalue_internal;
		this.kfOFvalue = kfOFvalue_internal;
		this.kfIMUvalue = kFIMUvalue_internal;
		this.KFsample = KFsample_internal;
		double [][] temp = {
				{1*KFcovar_internal, 0, 0, 0, 0, 0},
				{0, 1*KFcovar_internal, 0, 0, 0, 0},
				{0, 0, 1*KFcovar_internal, 0, 0, 0},
				{0, 0, 0, 1*KFcovar_internal, 0, 0},
				{0, 0, 0, 0, 1*KFcovar_internal, 0},
				{0, 0, 0, 0, 0, 1*KFcovar_internal}
				};
		p_prev = temp;
	}
	
	public static double[][] addition(double[][] matrixA, double[][] matrixB)
	{
		double[][] answermatrix = new double[matrixA.length][matrixA[0].length];
		for(int i=0;i<matrixA.length;i++){    
			for(int j=0;j<matrixA[0].length;j++){    
			answermatrix[i][j]=matrixA[i][j]+matrixB[i][j];    //use - for subtraction   
			}     
			}    
		return answermatrix;
	}
	
	private double[][] scalarmultiply(double[][] matrixA, double multiplier)
	{
		double[][] answermatrix = new double[matrixA.length][matrixA[0].length]; 
		for(int r = 0; r<matrixA.length; r++)
		{
			for (int c = 0; c<matrixA[0].length; c++)
			{
				answermatrix[r][c] = matrixA[r][c] * multiplier;
			}
		}
		return answermatrix;
	}
	
	public static double[][] multiplication(double[][] matrixA, double[][] matrixB)
	{
		double[][] answermatrix = new double[matrixA.length][matrixB[0].length];
		if (matrixA[0].length == matrixB.length)
		{
			for(int i=0;i<matrixA.length;i++)
			{    
				for(int j=0;j<matrixB[0].length;j++)
				{    
					answermatrix[i][j]=0.0;      
					for(int k=0;k<matrixA[0].length;k++)      
					{      
						answermatrix[i][j]+=matrixA[i][k]*matrixB[k][j];
					}
				}
			}
		}
		
		
		return answermatrix;
	}
	
	public static double[][] transpose(double[][] matrixA)
	{
		double transpose[][] = new double[matrixA[0].length][matrixA.length];
		for(int i=0;i<matrixA.length;i++){    
			for(int j=0;j<matrixA[0].length;j++){    
			transpose[j][i]=matrixA[i][j];  // NOOOOOOOO
			//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			// looks like you switches the indices for transpose and matrixA.
			// for instance i goes from 0 to matrixA.rows which is not neccecarily equal to transpose.rows
			// j goes from 0 to matrixA.cols; matrixA[j][i] here j is used as row index and i as col index
			//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			// That is an oopsie
			}    
		} 	
		return transpose;
	}
	
	private double[][] gauss(double[][] matrix, int row, int column)
	{
		double[][] inverse = matrix;
		int rowcount = row;
		int columncount =column;
		if (row == matrix.length) return inverse;
		if (inverse[row][column] != 1.0) 
		{
			double multiplier = 1/inverse[row][column];
			//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			// What if inverse[row][col] == 0
			//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			// This thing traverses the diagonal element of the matrix, an invertible matrix cannot have 0 in the diagonal element. 
			for(int c = 0; c<inverse[0].length; c++){
				inverse[row][c] = inverse[row][c]*multiplier;
			}
		}
		for (int i = 0; i<inverse.length; i++) {
			if (i != rowcount) {
				//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
				double multiplier = inverse[i][columncount] / inverse[rowcount][columncount] /*isnt inv[r][c] now 1?*/ * -1;
				//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
				for (int c = 0; c < inverse[0].length; c++)	inverse[i][c] = inverse[i][c] + inverse[rowcount][c]*multiplier;
			}
		}
		rowcount++;
		columncount++;
		gauss(inverse,rowcount,columncount);
		return inverse;
	}
	
	private double[][] inverse(double[][] matrix)
	{
		double[][] bigmatrix = new double[matrix.length][matrix[0].length*2];
		double[][] finished = new double[bigmatrix.length][bigmatrix[0].length/2];
		int rowcount = 0;
		int columncount = 0;
		while (rowcount < matrix[0].length)
		{
			bigmatrix[rowcount][columncount+matrix[0].length] = 1;
			//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			// are the off diag elements of bigmatrix always initialized as 0?
			// i am unsure if java zeros new arrays, but I would not take any chances and zero them anyway
			//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			// Java primitives when initialized are zeros.
			for (int j = 0; j<matrix[0].length; j++)
			{	
				bigmatrix[rowcount][j] = matrix[rowcount][j];
			}
			rowcount++;
			columncount++;
		}
		bigmatrix = gauss(bigmatrix,0,0);
		for (int i = 0; i<finished.length; i++) {
			for (int j = 0; j<finished[0].length; j++) {
				finished[i][j] = bigmatrix[i][j+finished[0].length];
			}
		}
		return finished;
	}
	
	public double[][] calculate(double UWBXinput, double UWBYinput, double OFXinput, double OFYinput, double IMUXinput, double IMUYinput)
	{
		
		Double Dt = KFsample;
		//System.out.println(Dt);
		int z = 1;
		int vx = -(xflow)*z;
		int vy = -(yflow)*z;
		
		// Q matrix
		
		double[][] Qmatrix = { 
                {0.03, 0, 0, 0, 0, 0},
                {0, 0.03, 0, 0, 0, 0},
                {0.02, 0, 0.01, 0, 0, 0},
                {0, 0.02, 0, 0.01, 0, 0},
                {0, 0, 0, 0, 0.003, 0},
                {0, 0, 0, 0, 0, 0.003}
			};
		
		// R matrix
		
		double[][] Rmatrix = {
				{kfUWBvalue, 0, 0, 0, 0, 0},
				{0, kfUWBvalue, 0, 0, 0, 0},
				{0, 0, kfOFvalue, 0, 0, 0},
				{0, 0, 0, kfOFvalue, 0, 0},
				{0, 0, 0, 0, kfIMUvalue, 0},
				{0, 0, 0, 0, 0, kfIMUvalue}
			};
		
		// System matrix AD
		
		double[][] ADmatrix = {
				{1, 0, 1*Dt, 0, 0.5*1*Dt*Dt, 0},
				{0, 1, 0, 1*Dt, 0, 0.5*1*Dt*Dt},
				{0, 0, 1, 0, 1*Dt, 0},
				{0, 0, 0, 1, 0, 1*Dt},
				{0, 0, 0, 0, 1, 0},
				{0, 0, 0, 0, 0, 1}
			};
		
		// output matrix Cd
		double[][] Cdmatrix = {
				{1, 0, 0, 0, 0, 0},
				{0, 1, 0, 0, 0, 0},
				{0, 0, 1, 0, 0, 0},
				{0, 0, 0, 1, 0, 0},
				{0, 0, 0, 0, 1, 0},
				{0, 0, 0, 0, 0, 1}
			};
		
		//updated inputs L
		double[] L = {1,1,1,1,1,1};
		
		//measurements
		double[][] Y = {
				{UWBXinput},
				{UWBYinput},
				{OFXinput},
				{OFYinput},
				{IMUXinput},
				{IMUYinput}};		
		
		
		//Main algorithmn
		
		double[][] P;
		double[][] x_hat;
		
		//initial state
		P = addition(multiplication(multiplication(ADmatrix,p_prev),transpose(ADmatrix)),Qmatrix);
		//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		// incorrect, it is Ad*p_prev*Ad^T + Q; or in otherwords use transpose instead of inverse
		// but are these P and x_hat even needed considering you calculate them again in the correct way below?
		//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		// These ones are indeed not needed, they are used in the matlab cuz they are expecting only update from one sensor
		// in which case, they update the other values in the matrix (the ones that didnt get sensor input), with the above formula.
		x_hat = multiplication(ADmatrix, x_prev);
		
		//State covar prediction
		
		double[][] P_pred = addition(multiplication(multiplication(ADmatrix, p_prev),transpose(ADmatrix)),Qmatrix);
		
		//state prediction
		double[][] x_pred = multiplication(ADmatrix, x_prev);
		
//		System.out.println("prediction");
//		for (int r = 0; r<P_pred.length; r++)
//		{
//			for (int c = 0; c<P_pred[0].length; c++)
//			{
//				System.out.print(P_pred[r][c] + " ") ;
//			}
//			System.out.println();
//		}
		
		//residual covariance
		double[][] Cm = {
				{1, 0, 0, 0, 0, 0},
				{0, 1, 0, 0, 0, 0},
				{0, 0, 1, 0, 0, 0},
				{0, 0, 0, 1, 0, 0},
				{0, 0, 0, 0, 1, 0},
				{0, 0, 0, 0, 0, 1}};
		double[][] Rm = Rmatrix;
				
		double[][] S = addition(multiplication(multiplication(Cm, P_pred),transpose(Cm)),Rm);
		
//		System.out.println("residual");
//		for (int r = 0; r<S[0].length; r++)
//		{
//			for (int c = 0; c<S.length; c++)
//			{
//				System.out.print(S[r][c] + " ") ;
//			}
//			System.out.println();
//		}
		
		//Kalman gain
		double[][] K = multiplication(multiplication(P_pred, transpose(Cm)),inverse(S));
		
//		System.out.println("kalman gain");
//		for (int r = 0; r<K[0].length; r++)
//		{
//			for (int c = 0; c<K.length; c++)
//			{
//				System.out.print(K[r][c] + " ") ;
//			}
//			System.out.println();
//		}
		
		//state update
		x_hat = addition(x_pred, multiplication(K, (addition(Y,scalarmultiply(multiplication(Cm, x_pred),-1.0)))));
		//x_hat = multiplication(K, (addition(Y,scalarmultiply(multiplication(Cm, x_pred),-1.0))));
		
		//double[][] test1 = multiplication (Cm,x_pred);
		

//		
//		System.out.println("state update");
//		for (int r = 0; r<x_hat.length; r++)
//		{
//			for (int c = 0; c<x_hat[0].length; c++)
//			{
//				System.out.print(x_hat[r][c] + " ") ;
//			}
//			System.out.println();
//		}
//		
		
		//state covar update
		double[][] identity = {
				{1, 0, 0, 0, 0, 0},
				{0, 1, 0, 0, 0, 0},
				{0, 0, 1, 0, 0, 0},
				{0, 0, 0, 1, 0, 0},
				{0, 0, 0, 0, 1, 0},
				{0, 0, 0, 0, 0, 1}};

		double[][] left = addition(identity, scalarmultiply(multiplication(K,Cm),-1.0));
		double[][] right = multiplication(multiplication(K, Rm),transpose(K));
		
		P = addition(multiplication(multiplication(left, P_pred), transpose(left)),right);
		//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		// incorrect
		// right should be K*Rm*K^T not the inverse of K
		// same goes for P = left*p_red*left^T + right and again the transpose not the inverse of left
		//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		// OOPsie

//		System.out.println("Covar update");
//		for (int r = 0; r<P.length; r++)
//		{
//			for (int c = 0; c<P[0].length; c++)
//			{
//				System.out.print(P[r][c] + " ") ;
//			}
//			System.out.println();
//		}

		
		//x_hat(isnan(x_hat)) = 0*x_prev(isnan(x_hat));
		//P(isnan(P)) = 0*P_prev(isnan(P));
		
		for (int i = 0; i<x_hat.length; i++)
		{
			if (Double.isNaN(x_hat[i][0]))
			{
				x_hat[i][0] = 0;
			}
		}
		
		for (int r = 0; r<P.length; r++)
		{
			for (int c = 0; c<P[0].length; c++)
			{
				if (Double.isNaN(P[r][c]))
				{
				//	System.out.println("true");
					P[r][c] = 0;
				}
			}
		}

		x_prev = x_hat;
		p_prev = P;
					
		return x_hat;
	}
	
	public static void main(String[] argz)
	{
		kalmanfilter kf = new kalmanfilter(80,8000,10,10000000000L,0.02);
		double[][] answer = kf.calculate(0.5, 0.5, 1, 1, 1, 1);
		NormalDistribution ND = new NormalDistribution(1, 100);
		System.out.println(ND.sample());
////		System.out.println(answer.length);
////		System.out.println(answer[0].length);
//		System.out.println("Final output");
//
//		for (int r = 0; r<answer[0].length; r++)
//		{
//			for (int c = 0; c<answer.length; c++)
//			{
//				System.out.print(answer[r][c] + " ") ;
//			}
//			System.out.println();
//		}
//		answer = kf.calculate(0.51, 0.51, 1.01, 1.01, 1, 1);
		//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		// hmmmm...
		// the 0.15 values are the first time-derivative of the 0.6 value, at a time-step of 0.02
		// according to me that would make: (nextval - prevval) / dt = (0.6-0.5)/0.02 = 5 and not 0.15
		// same applies to the second time-derivative of position.
		//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		// Right, of course, wasn't taking the DT in to consideration.
////		
////		System.out.println("Final output");
////		for (int r = 0; r<answer[0].length; r++)
////		{
////			for (int c = 0; c<answer.length; c++)
////			{
////				System.out.print(answer[r][c] + " ") ;
////			}
////			System.out.println();
////		}
////		
//		answer = kf.calculate(0.5201, 0.5201, 1.02, 1.02, 1, 1);
////		
////		System.out.println("Final output");
////		for (int r = 0; r<answer[0].length; r++)
////		{
////			for (int c = 0; c<answer.length; c++)
////			{
////				System.out.print(answer[r][c] + " ") ;
////			}
////			System.out.println();
////		}
////		
//		answer = kf.calculate(0.5303, 0.5303, 1.03, 1.03, 1, 1);
////		
////		System.out.println("Final output");
////		for (int r = 0; r<answer[0].length; r++)
////		{
////			for (int c = 0; c<answer.length; c++)
////			{
////				System.out.print(answer[r][c] + " ") ;
////			}
////			System.out.println();
////		}
////		
////		
////		System.out.println("Final output");
////		for (int r = 0; r<answer[0].length; r++)
////		{
////			for (int c = 0; c<answer.length; c++)
////			{
////				System.out.print(answer[r][c] + " ") ;
////			}
////			System.out.println();
////		}
	}
}
