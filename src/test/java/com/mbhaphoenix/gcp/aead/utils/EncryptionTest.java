package com.mbhaphoenix.gcp.aead.utils;

import com.google.crypto.tink.Aead;
import com.google.crypto.tink.KeysetHandle;
import com.google.crypto.tink.aead.AeadConfig;
import com.google.crypto.tink.aead.AeadKeyTemplates;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.junit.Before;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.junit.Assert.*;

public class EncryptionTest {

    private KeysetHandle keysetHandle;
    private Aead aead;

    private static final String STRING_TO_ENCRYPT = "a string";
    private static final String AD_STRING = "ad";

    @Before
    public void setUp() throws Exception {
        AeadConfig.register();
        keysetHandle = KeysetHandle.generateNew(AeadKeyTemplates.AES256_GCM);
        aead = keysetHandle.getPrimitive(Aead.class);
    }

    @Test
    public void testDifferentResultsForEncryptionAndSameResultForDecryption() throws Exception {
        byte[] ciphertext1 = aead.encrypt(STRING_TO_ENCRYPT.getBytes(StandardCharsets.UTF_8), AD_STRING.getBytes(StandardCharsets.UTF_8));
        System.out.println("ciphertext 1 :" + Base64.getEncoder().encodeToString(ciphertext1));
        byte[] ciphertext2 = aead.encrypt(STRING_TO_ENCRYPT.getBytes(StandardCharsets.UTF_8), AD_STRING.getBytes(StandardCharsets.UTF_8));
        System.out.println("ciphertext 2: " + Base64.getEncoder().encodeToString(ciphertext2));
        assertNotEquals(Base64.getEncoder().encodeToString(ciphertext1), Base64.getEncoder().encodeToString(ciphertext2));
        byte[] plaintextBytes1 = aead.decrypt(ciphertext1, AD_STRING.getBytes());
        byte[] plaintextBytes2 = aead.decrypt(ciphertext2, AD_STRING.getBytes());
        System.out.println("ciphertext 1: " + new String(plaintextBytes1));
        System.out.println("ciphertext 2: " + new String(plaintextBytes2));
        assertArrayEquals(plaintextBytes1, plaintextBytes2);
    }


}