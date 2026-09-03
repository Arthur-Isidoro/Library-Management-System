package com.seuprojeto.library;

public class TesteEnv {
    public static void main(String[] args) {
        System.out.println("Dados do banco de dados:");
        System.out.println("Usuario: " + System.getenv("DB_USUARIO"));
        System.out.println("Senha: " + System.getenv("DB_SENHA"));
    }
}