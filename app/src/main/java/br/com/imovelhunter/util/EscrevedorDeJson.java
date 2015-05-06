package br.com.imovelhunter.util;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import br.com.imovelhunter.annotations.NaoJSON;


public class EscrevedorDeJson {
	
	private SimpleDateFormat formato;
	
	
	
	
	/** 
	 * Escreve um objeto em json, s� que de maneira melhor, o tipo do Date deve ser do util e n�o do sql.
	 * @param objeto
	 * @return
	 * @throws Exception 
	 */
	public String escreverJson(Object objeto) throws Exception{
		
		if(!(objeto instanceof ObjetoJSON)){
			throw new Exception("O objeto n�o extende um ObjetoJSON");
		}
		
		//Buffer que vai ficar concatenando
		StringBuffer stb = new StringBuffer();
		
		stb.append("{");
		
		//Pega a classe do objeto passado
		Class<?> classe = objeto.getClass();
		
		//Pega os campos declarados da classe
		Field[] campos = classe.getDeclaredFields();
		
		if(campos != null){
			int tamCampos = campos.length;
			
			Boolean ultimoCampo = false;
			
			//Percorre os campos declarados
			for(int f = 0 ; f < tamCampos ; f++){
				
				//Pega o campo
				Field campo = campos[f];
				
				//Deixa o campo acessivel, caso o campo seja privado
				campo.setAccessible(true);
				
				Object objetoValor = null;	
				
				Class<?> tipo = campo.getType();
				
				
				try{
					//Pega o valor daquele campo, do objeto.
					objetoValor = campo.get(objeto);
				}catch(Exception ex){
					
				}		
				
				//Pega o nome do campo
				String nomeCampo = campo.getName();
				
				//Se o campo for o de serializable, ele seja o objetoValor nulo para que seja ignorado pelo outros processos.
				if(nomeCampo.equals("serialVersionUID")){
					objetoValor = null;
				}
				
				//Se o campo tiver a annotarion NAOJSON, ele deixa o objetoValor nullo, vazendo com que o campo n�o seja inserido no json
				if(campo.isAnnotationPresent(NaoJSON.class)){
					objetoValor = null;
				}
				
				if(objetoValor != null){
				
					//Valida se ele est� no ultimo campo, caso esteja no ultimo campo, ele n�o insere uma v�rgula no final de cada itera��o
					if(f + 1 == tamCampos){					
						ultimoCampo = true;
					}			
					
					//Declara uma categoria do JSONObject
					stb.append("\""+nomeCampo+"\":");
					
					//Se for data
					if(objetoValor instanceof Date){
						try{
							if(formato != null){
								stb.append("\""+formato.format(objetoValor)+"\"");
								
							}else{
								stb.append("\""+objetoValor+"\"");
							}
						}
						catch(Exception ex){
							
						}
					}			
					//Se for uma cole��o, lista, array, etc...
					else if(objetoValor instanceof Collection){
						stb.append("[");
					
					/*T� pegando, mas deve ser tratado como umm Collection e n�o como um List
						List<?> lista = (List<?>)campo.get(objeto);
						if(lista != null){
							int tam = lista.size();
							
							for(int i = 0 ; i < tam ; i++){
								Object o = lista.get(i);
								if(i + 1 == tam){
									stb.append(o.toString());
								}else{
									stb.append(o.toString());
									stb.append(",");
								}
							}
						}
					*/
						Object[] lista = ((Collection<?>)campo.get(objeto)).toArray();
						
						if(lista != null){
							int tam = lista.length;
							
							for(int i = 0 ; i < tam ; i++){
								Object o = lista[i];
								if(i + 1 == tam){
									stb.append(o.toString());
								}else{
									stb.append(o.toString());
									stb.append(",");
								}
							}
						}
						
						stb.append("]");
					}				
					else{		
						//Caso seja enum
						if(objetoValor instanceof Enum){
								String valor = ((Enum)objetoValor).name();
								
								if(valor != null){
									stb.append("\""+valor+"\"");
								}
						}					
						
						//Caso seja uma String
						else if(objetoValor instanceof String){
							stb.append("\""+objetoValor+"\"");						
						}
						//Caso seja n�mero
						else if(objetoValor instanceof Integer || objetoValor instanceof Float || objetoValor instanceof Double || objetoValor instanceof BigDecimal || objetoValor instanceof BigInteger || objetoValor instanceof Short || objetoValor instanceof Number){
							stb.append(objetoValor);											
						}
						//Case seja um boleano
						else if(objetoValor instanceof Boolean){
							stb.append("\""+objetoValor+"\"");
						}
						//Se for um objeto criado pela pessoa
						else{
							stb.append(objetoValor.toString());
						}
					}				
					if(!ultimoCampo){
						stb.append(",");
					}
					campo.setAccessible(false);
				
				}
				
			}
			
			
		}
		
		//Deleta caso tenha uma v�rgula no final do buffer
		if(stb.length() > 0 && stb.charAt(stb.length() - 1) == ','){
			stb.delete(stb.length() - 1,stb.length());
		}
		
		stb.append("}");
		
		return stb.toString();		
	}
	
	/**
	 * Ler um objeto json e seta os dados no objeto de acordo com que est� na string json
	 * @param jsonObject
	 * @param objeto
	 */
	public void lerJson(String jsonObject,Object objeto){	
		
		try{
			
			if(!(objeto instanceof ObjetoJSON)){
				throw new Exception("O objeto n�o extende um ObjetoJSON");
			}
			
			JSONObject objetoJson = new JSONObject(jsonObject);
		
			Field[] campos = objeto.getClass().getDeclaredFields();
			
			Boolean ultimoCampo = false;
			
			int tam = campos.length;
			
			for(int i = 0 ; i < tam ; i++){
				if(i + 1 == tam){
					ultimoCampo = true;
				}
				
				
				Field campo = campos[i];
				
				campo.setAccessible(true);
				
				String nomeDoCampo = campo.getName();
				
				if(objetoJson.has(nomeDoCampo)){						
					
						try{
						String tipoDoCampo = campo.getType().getSimpleName();
						
						Class<?> tipo = campo.getType();
						
						//Se for uma data
						if(tipo.isAssignableFrom(Date.class)){
							String valorData = objetoJson.getString(nomeDoCampo);
							try{															
								Date dataD = this.formato.parse(valorData);
								campo.set(objeto,dataD);
							}catch(Exception ex){
								try{
									SimpleDateFormat sdf = new SimpleDateFormat();
									campo.set(objeto,sdf.parseObject(valorData));
									
								}catch(Exception exB){
									
								}
							}
							
						}						
						//Se for uma cole��o
						else if(Collection.class.isAssignableFrom(tipo)){	
							
							ParameterizedType pT = (ParameterizedType)campo.getGenericType();
							Class<?> classeDaColecao = (Class<?>)pT.getActualTypeArguments()[0];	
							
							JSONArray colecao = objetoJson.getJSONArray(nomeDoCampo);
							
							Collection c = new ArrayList();
							
							int tamColecao = colecao.length();
							
							for(int j = 0 ; j < tamColecao ; j++){
								
								JSONObject obJ = colecao.getJSONObject(j);
								
								String stringJsonColecao = obJ.toString();
								
								Object objetoColecao = classeDaColecao.newInstance();
								
								((ObjetoJSON)objetoColecao).parse(stringJsonColecao);
								
								c.add(objetoColecao);
								
							}
							
							campo.set(objeto,c);										
							
						}
						//Se for um n�mero
						else if(tipoDoCampo.contains("Long") || tipoDoCampo.contains("long")){
							campo.set(objeto,objetoJson.getLong(nomeDoCampo));
						}else if(tipoDoCampo.contains("Integer") || tipoDoCampo.contains("int")){
							campo.set(objeto,objetoJson.getInt(nomeDoCampo));
						}else if(tipoDoCampo.contains("Double") || tipoDoCampo.contains("double")){
							campo.set(objeto,objetoJson.getDouble(nomeDoCampo));
						}else if(tipoDoCampo.contains("Float") || tipoDoCampo.contains("float")){
							campo.set(objeto,objetoJson.getDouble(nomeDoCampo));
						}
						//Se for uma String
						else if(tipo.isAssignableFrom(String.class)){
							campo.set(objeto,objetoJson.getString(nomeDoCampo));
						}
						//Se for um booleano
						else if(Boolean.class.isAssignableFrom(tipo)){
							try{
								campo.set(objeto,Boolean.valueOf(objetoJson.getString(nomeDoCampo)));
							}catch(Exception ex){
								
							}
						}
						//Se for um enum
						else if(tipo.isEnum()){									
							
							String valor = objetoJson.getString(nomeDoCampo);
							
							Enum<?> enume = Enum.valueOf((Class<? extends Enum>)tipo,valor);					
							
							campo.set(objeto,enume);
						}
						//Outro objeto
						else{
							JSONObject objetoObjeto = objetoJson.getJSONObject(nomeDoCampo);
							
							Object obji = tipo.newInstance();
							
							String stringObji = objetoObjeto.toString();
							
							((ObjetoJSON)obji).parse(stringObji);
							
							campo.set(objeto,obji);
						}
						
						campo.setAccessible(false);
					}
					catch(Exception ex){
						ex.printStackTrace();
					}
				}	
			
		}
		
		
		}
		catch(Exception ex){
			
		}
		
		
		
	
	}
	

	
	public void putDateFormar(String dateFormat){
		this.formato = new SimpleDateFormat(dateFormat);
	}
	

}
