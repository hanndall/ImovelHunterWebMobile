package br.com.imovelhunter.util;

import java.lang.reflect.ParameterizedType;

public class ObjetoJSON<T> {
	
	private Class<T> classe;	
	
	
	private EscrevedorDeJson esc;
	
	public ObjetoJSON(){
		ParameterizedType pT = (ParameterizedType)this.getClass().getGenericSuperclass();
		this.classe = (Class<T>)pT.getActualTypeArguments()[0];		
		
		this.esc = new EscrevedorDeJson();
		
		this.esc.putDateFormar("dd/MM/yyyy HH:mm:ss");
	}
	
	public void putSimpleDateFormat(String format){
		this.esc.putDateFormar(format);
	}
	
	@Override
	public String toString() {
		try{
			
			return this.esc.escreverJson(this);
			
		}catch(Exception ex){
			ex.printStackTrace();
			return null;
		}
	}
	
	
	public void parse(String jsonString){
		esc.lerJson(jsonString,this);
	}
	
	
	

}
