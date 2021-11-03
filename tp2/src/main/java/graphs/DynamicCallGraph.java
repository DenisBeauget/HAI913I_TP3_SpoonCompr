package graphs;

import java.io.IOException;
import java.util.ArrayList;

import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.SuperMethodInvocation;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import utility.Utility;
import visitors.ClassDeclarationsCollector;
import visitors.MethodDeclarationsCollector;
import visitors.MethodInvocationsCollector;
import models.ClassAndContent;
import models.ClassCouple;
import processors.ProcessorClustering;

public class DynamicCallGraph extends CallGraph  {
	/*
	 * /home/hayaat/Desktop/Master/M1/Java/TP4/src/
	 */

	/* CONSTRUCTOR */
	private DynamicCallGraph(String projectPath) {
		super(projectPath);
	}
	
	/* * * * * * * * * * * *
	 * 
	 *  METHODS PRIVATE
	 *  
	 *  * * * * * * * * * */
	private boolean addMethodAndInvocations(TypeDeclaration cls, MethodDeclaration method) {
		if(method.getBody() != null) {
			String methodName = Utility.getMethodFullyQualifiedName(cls, method);
			this.addMethod(methodName);
			
			MethodInvocationsCollector invocationCollector = new MethodInvocationsCollector();
			this.addInvocations(cls, method, methodName, invocationCollector);
			this.addSuperInvocations(methodName, invocationCollector);
		}
		
		return method.getBody() != null;
	}
	
	private void addInvocations(TypeDeclaration cls, MethodDeclaration method, 
			String methodName, MethodInvocationsCollector invocationCollector) {
		method.accept(invocationCollector);
		
		for (MethodInvocation invocation: invocationCollector.getMethodInvocations()) {
			String invocationName = getMethodInvocationName(cls, invocation);
			this.addMethod(invocationName);
			this.addInvocation(methodName, invocationName);
		}
	}

	private String getMethodInvocationName(TypeDeclaration cls, MethodInvocation invocation) {
		Expression expr = invocation.getExpression();
		String invocationName = "";
		
		if (expr != null) {
			ITypeBinding type = expr.resolveTypeBinding();
			
			if (type != null) 
				invocationName = type.getQualifiedName() + "::" + invocation.getName().toString();
			else
				invocationName = expr + "::" + invocation.getName().toString();
		}
		
		else
			invocationName = Utility.getClassFullyQualifiedName(cls) 
				+ "::" + invocation.getName().toString();
		
		return invocationName;
	}
	
	private void addSuperInvocations(String methodName, MethodInvocationsCollector invocationCollector) {
		for (SuperMethodInvocation superInvocation: invocationCollector.getSuperMethodInvocations()) {
			String superInvocationName = superInvocation.getName().getFullyQualifiedName();
			this.addMethod(superInvocationName);
			this.addInvocation(methodName, superInvocationName);
		}
	}
	/* * * * * * * * * * * *
	 * 
	 *  METHODS PRIVATE ADDED
	 *  
	 *  * * * * * * * * * */
	

	
	/* * * * * * * * * * * *
	 * 
	 *  METHODS PUBLIC
	 *  
	 *  * * * * * * * * * */
	public static DynamicCallGraph createCallGraph(String projectPath, CompilationUnit cUnit) {
		DynamicCallGraph graph = new DynamicCallGraph(projectPath);
		ClassDeclarationsCollector classCollector = new ClassDeclarationsCollector();
		cUnit.accept(classCollector);
		
		for(TypeDeclaration cls: classCollector.getClasses()){
			MethodDeclarationsCollector methodCollector = new MethodDeclarationsCollector();
			cls.accept(methodCollector);
			
			for(MethodDeclaration method: methodCollector.getMethods())
				graph.addMethodAndInvocations(cls, method);
		}
		
		return graph;
	}
	
	/*
	 * AJOUT DE LA STRUCTURE ArrayList<ClassAndContent> PENDANT L'APPEL
	 * */
	public static DynamicCallGraph createCallGraph(String projectPath) 
			throws IOException {
		DynamicCallGraph graph = new DynamicCallGraph(projectPath);
		
		for(CompilationUnit cUnit: graph.parser.parseProject()) {
			DynamicCallGraph partial = DynamicCallGraph.createCallGraph(projectPath, cUnit);
			graph.addMethods(partial.getMethods());
			graph.addInvocations(partial.getInvocations());
		}
		
		//System.out.println(graph.getModel().toString());	

		return graph;
	}
	// /home/hayaat/Desktop/Master/M2/Java2021/HAI913I_badSmell/src/
	// /home/hayaat/Desktop/Master/M1/Java/TP4/src/
	// /home/hayaat/Desktop/Master/M1/Java/HMIN210/TP1RMI/src/
	// /home/hayaat/Desktop/Master/M2/Git/HAI913I_TP3_SpoonCompr/design_patterns/src/
	/* * * * * * * * * * * *
	 * 
	 *  METHODS PUBLIC ADDED
	 *  
	 *  * * * * * * * * * */

}
