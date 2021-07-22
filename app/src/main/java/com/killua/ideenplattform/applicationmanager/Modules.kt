package com.killua.ideenplattform.applicationmanager

import android.content.Context
import androidx.room.Room
import com.killua.ideenplattform.R
import com.killua.ideenplattform.data.caching.CategoryDao
import com.killua.ideenplattform.data.caching.DbStructure
import com.killua.ideenplattform.data.caching.IdeaDao
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
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
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
    fun categoryDaoProvider(dbStructure: DbStructure) = dbStructure.categoryDao
    single { categoryDaoProvider(get()) }
    single { ideaDaoProvider(get()) }
    single { userDaoProvider(get()) }

    fun providerMainRepository(
        api: ApiServices,
        ideaDao: IdeaDao,
        userDao: UserDao,
        categoryDao: CategoryDao,
        context:Context
    ): MainRepository {
        return MainRepositoryImpl(
            api,
            userDao,
            ideaDao,
            categoryDao,
            context
        )
    }
        single { providerMainRepository(get(), get(), get(),get(),androidContext()) }


// Specific viewModel pattern to tell Koin how to build CountriesViewModel
        single {
            HomeViewModel(androidContext())
        }



    fun provideHttpClient(sharedPreferencesHandler: SharedPreferencesHandler): OkHttpClient {
        val user = sharedPreferencesHandler.userLoader

        var authToken = ""
        user?.let {
            authToken=Credentials.basic(it.email, it.password)
        }
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
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
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

