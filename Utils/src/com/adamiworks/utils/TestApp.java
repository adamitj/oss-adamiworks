package com.adamiworks.utils;

import com.adamiworks.utils.crypto.CryptoMode;
import com.adamiworks.utils.crypto.CryptoUtils;

public class TestApp {

	public static void main(String[] args) {
		String texto = "Tiago José Adami, Analice Cristina Lewinski Lopes, Laura Rafaela Lopes Adami";
		String password = "IdontKnow0ClowsG1v3meFre4AdkingNootZBicausiTheyA$%0)sdjneifh";

		try {
			CryptoUtils aes = new CryptoUtils(CryptoMode.AES, password);
			CryptoUtils des = new CryptoUtils(CryptoMode.DES, password);

			System.out.println(CryptoUtils.getHash8String(password));
			System.out.println(CryptoUtils.getHash16String(password));

			String eAes = aes.encrypt(texto);
			String eDes = des.encrypt(texto);
			
			System.out.println(eAes);
			System.out.println(eDes);
			
			System.out.println(aes.decrypt(eAes));
			System.out.println(des.decrypt(eDes));
			
			

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
