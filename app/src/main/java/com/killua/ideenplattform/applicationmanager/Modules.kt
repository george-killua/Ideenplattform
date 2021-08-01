package com.killua.ideenplattform.applicationmanager

import android.content.Context
import androidx.room.Room
import com.ashokvarma.gander.GanderInterceptor
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
import com.killua.ideenplattform.ui.details.DetailViewModel
import com.killua.ideenplattform.ui.home.HomeViewModel
import com.killua.ideenplattform.ui.newidee.ManagementIdeeViewModel
import com.killua.ideenplattform.ui.profile.ProfileFragment
import com.killua.ideenplattform.ui.profile.ProfileViewModel
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SupportFactory
import okhttp3.Credentials
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


val databaseModule by lazy {
    module {
        fun dbProvider(context: Context): DbStructure {
            val factory = SupportFactory(SQLiteDatabase.getBytes("nfjsdnfsd@fsdk221,.".toCharArray()))
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

    }
}
val httpModule by lazy {
    module {
        fun provideHttpClient(
            sharedPreferencesHandler: SharedPreferencesHandler,
            context: Context
        ): OkHttpClient {
            val user = sharedPreferencesHandler.userLoader
            val logging = HttpLoggingInterceptor()
            logging.setLevel(HttpLoggingInterceptor.Level.BODY)
            var authToken = ""
            user?.let {
                authToken = Credentials.basic(it.email, it.password)
            }
            val client = OkHttpClient.Builder()
            client.authenticator(AuthenticationInterceptor(authToken))
                .addInterceptor(
                    GanderInterceptor(context).showNotification(true).redactHeader("Authorization")
                        .redactHeader("Cookie").retainDataFor(GanderInterceptor.Period.ONE_WEEK)
                )

            client.connectTimeout(10, TimeUnit.SECONDS)
            client.writeTimeout(10, TimeUnit.SECONDS)
            client.readTimeout(30, TimeUnit.SECONDS)
            client.addInterceptor(logging)

            return client.build()
        }
        single { provideHttpClient(get(), androidContext()) }
        single { HttpClient(get()) }
    }
}

val apiModule by lazy {
    module {


        fun provideRetrofit(client: OkHttpClient, baseUrl: String): Retrofit {
            return Retrofit.Builder()
                .baseUrl(baseUrl)
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()

        }
        single {
            val baseUrl = androidContext().getString(R.string.api_url)
            provideRetrofit(get(), baseUrl)
        }
        fun servicesApi(retrofit: Retrofit): ApiServices =
            retrofit.create(ApiServices::class.java)
        single { servicesApi(get()) }

    }
}

val repoModule by lazy {
    module {
        fun providerMainRepository(
            api: ApiServices,
            ideaDao: IdeaDao,
            userDao: UserDao,
            categoryDao: CategoryDao,
            context: Context
        ): MainRepository {
            return MainRepositoryImpl(
                api,
                userDao,
                ideaDao,
                categoryDao,
                context
            )
        }
        single { providerMainRepository(get(), get(), get(), get(), androidContext()) }
    }
}

val moduleBuilder by lazy {
    module {

        single {
            SharedPreferencesHandler(androidContext())
        }
        single { HomeViewModel(get()) }
        single { ManagementIdeeViewModel(androidContext(), get()) }
        single { DetailViewModel(get()) }
        viewModel { ProfileViewModel(get()) }

    }
}

