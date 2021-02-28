package com.token.tokenator.di

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class DataStoreLowercase

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class DataStoreNoRepeat

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class DataStoreNumeric

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class DataStoreSpecialCharacters

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class DataStoreUppercase