package com.ninepm.english.learn.di

import android.content.Context
import com.ninepm.english.learn.firebase.FirebaseAuthImpl
import com.ninepm.english.learn.utils.AppExecutors

object Injection {
    fun provideFirebaseAuth(context: Context): FirebaseAuthImpl {
        val appExecutors = AppExecutors()

        return FirebaseAuthImpl.getInstance(appExecutors)
    }

//    fun provideTvRepository(context: Context): TvRepositoryImpl {
//        val database = Databases.getInstance(context)
//        val remoteDataSource = RemoteDataSource.getInstance(ApiHelpers())
//        val localDataSource = LocalDataSource.getInstance(database.filmDao())
//        val appExecutors = AppExecutors()
//
//        return TvRepositoryImpl.getInstance(remoteDataSource,localDataSource, appExecutors)
//    }
}