package au.edu.unsw.cse.data.api.infrastructure;

import javax.inject.Singleton;

import org.glassfish.hk2.api.InjectionResolver;
import org.glassfish.hk2.api.TypeLiteral;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.spi.internal.ValueFactoryProvider;
import org.mongodb.morphia.Datastore;

import com.mongodb.client.MongoDatabase;

import au.edu.unsw.cse.data.api.domain.abstracts.ClientRepository;
import au.edu.unsw.cse.data.api.domain.abstracts.EntityRepository;
import au.edu.unsw.cse.data.api.domain.abstracts.EntityTypeRepository;
import au.edu.unsw.cse.data.api.domain.abstracts.TokenRepository;
import au.edu.unsw.cse.data.api.domain.abstracts.UserRepository;
import au.edu.unsw.cse.data.api.domain.concrete.ClientRepositoryImp;
import au.edu.unsw.cse.data.api.domain.concrete.EntityRepositoryImp;
import au.edu.unsw.cse.data.api.domain.concrete.EntityTypeRepositoryImp;
import au.edu.unsw.cse.data.api.domain.concrete.TokenRepositoryImp;
import au.edu.unsw.cse.data.api.domain.concrete.UserRepositoryImp;
import au.edu.unsw.cse.data.api.security.AppUser;

public class DataApiBinder extends AbstractBinder {

	@Override
	protected void configure() {
		bindFactory(MongoFactory.class).to(MongoDatabase.class).in(Singleton.class);
		bindFactory(DatastoreFactory.class).to(Datastore.class).in(Singleton.class);
		bind(UserInfoFactoryProvider.class).to(ValueFactoryProvider.class).in(Singleton.class);
		bind(UserInfoFactoryProvider.InjectionResolver.class).to(new TypeLiteral<InjectionResolver<AppUser>>() {
		}).in(Singleton.class);
		bind(EntityRepositoryImp.class).to(EntityRepository.class);
		bind(EntityTypeRepositoryImp.class).to(EntityTypeRepository.class);
		bind(TokenRepositoryImp.class).to(TokenRepository.class);
		bind(UserRepositoryImp.class).to(UserRepository.class);
		bind(ClientRepositoryImp.class).to(ClientRepository.class);
	}
}
