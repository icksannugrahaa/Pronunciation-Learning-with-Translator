package com.ninepm.english.learn.di

import android.content.Context
import com.ninepm.english.learn.data.source.RemoteRepositoryImpl
import com.ninepm.english.learn.data.source.remote.RemoteDataSource
import com.ninepm.english.learn.firebase.FirebaseAuthImpl
import com.ninepm.english.learn.utils.ApiHelpers
import com.ninepm.english.learn.utils.AppExecutors

object Injection {
    fun provideFirebaseAuth(context: Context): FirebaseAuthImpl {
        val appExecutors = AppExecutors()

        return FirebaseAuthImpl.getInstance(appExecutors)
    }

    fun providePredict(): RemoteRepositoryImpl {
        val appExecutors = AppExecutors()
        val remoteDataSource = RemoteDataSource.getInstance(ApiHelpers())

        return RemoteRepositoryImpl.getInstance(remoteDataSource,appExecutors)
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