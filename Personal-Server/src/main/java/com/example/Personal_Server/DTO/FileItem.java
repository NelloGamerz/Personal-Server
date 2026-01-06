package com.example.Personal_Server.DTO;

public record FileItem(
    String name,
    String path,
    boolean isDirectory,
    long size
) {}
