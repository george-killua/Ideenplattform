package com.killua.ideenplattform.applicationmanager

import android.content.Context
import androidx.room.Room
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.killua.ideenplattform.R
import com.killua.ideenplattform.data.caching.DbStructure
import com.killua.ideenplattform.data.caching.IdeasDao
import com.killua.ideenplattform.data.caching.UserDao
import com.killua.ideenplattform.data.network.ApiServices
import com.killua.ideenplattform.data.network.HttpClient
import com.killua.ideenplattform.data.repository.MainRepository
import com.killua.ideenplattform.data.repository.MainRepositoryImpl
import com.killua.ideenplattform.data.utils.SharedPreferencesHandler
import com.killua.ideenplattform.ui.home.HomeViewModel
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SupportFactory
import okhttp3.Credentials
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


val moduleBuilder = module {
    fun dbProvider(context: Context): DbStructure {
        val factory =
            SupportFactory(SQLiteDatabase.getBytes("nfjsdnfsd@fsdk221,.".toCharArray()))
        return Room.databaseBuilder(
            context, DbStructure::class.java,
            "CurrentDBCALLY"
        ).openHelperFactory(factory)
            .fallbackToDestructiveMigration()
            .allowMainThreadQueries().build()
    }
    single { dbProvider(androidContext()) }
    fun ideaDaoProvider(dbStructure: DbStructure) = dbStructure.ideaDao
    fun userDaoProvider(dbStructure: DbStructure) = dbStructure.userDao
    single { ideaDaoProvider(get()) }
    single { userDaoProvider(get()) }

    fun providerMainRepository(
        api: ApiServices,
        ideasDao: IdeasDao,
        userDao: UserDao
    ): MainRepository {
        return MainRepositoryImpl(
            api,
            userDao,
            ideasDao
        )
    }
        single { providerMainRepository(get(), get(), get()) }


// Specific viewModel pattern to tell Koin how to build CountriesViewModel
        single {
            HomeViewModel(androidContext())
        }



    fun provideHttpClient(sharedPreferencesHandler: SharedPreferencesHandler): OkHttpClient {
        val user = sharedPreferencesHandler.userLoader
        assert(user == null)
        val authToken: String = Credentials.basic(user?.email!!, user.password)
        val client = OkHttpClient.Builder()
        client.addInterceptor(AuthenticationInterceptor(authToken))
        client.connectTimeout(10, TimeUnit.SECONDS)
        client.writeTimeout(10, TimeUnit.SECONDS)
        client.readTimeout(30, TimeUnit.SECONDS)

        return client.build()
    }

    fun provideRetrofit(client: OkHttpClient, baseUrl: String): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .client(client)
            .build()
    }

    fun servicesApi(retrofit: Retrofit): ApiServices =
        retrofit.create(ApiServices::class.java)
    single {
        SharedPreferencesHandler(androidContext())
    }
    single { provideHttpClient(get()) }
    single { HttpClient(get()) }
    single {
        val baseUrl = androidContext().getString(R.string.api_url)
        provideRetrofit(get(), baseUrl)
    }
    factory { servicesApi(get()) }

}

