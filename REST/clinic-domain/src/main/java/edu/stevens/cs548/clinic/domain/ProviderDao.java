package edu.stevens.cs548.clinic.domain;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

// TODO
@RequestScoped
@Transactional
public class ProviderDao implements IProviderDao {

	// TODO
	@Inject
	@ClinicDomainProducer.ClinicDomain
	private EntityManager em;

	// TODO
	@Inject
	private ITreatmentDao treatmentDao;

	private Logger logger = Logger.getLogger(ProviderDao.class.getCanonicalName());

	@Override
	public void addProvider(Provider provider) throws ProviderExn {
		// TODO Add to database, and initialize the provider aggregate with a treatment DAO.
		UUID pid = provider.getProviderId();
		Query query = em.createNamedQuery("CountProviderByProviderId").setParameter("providerId", pid);
		Long numExisting = (Long) query.getSingleResult();

		logger.info(String.format("Adding Provider with id %s, found %d existing records", pid.toString(), numExisting));

		if (numExisting < 1) {

			// Add to database, and initialize the patient aggregate with a treatment DAO.
			em.persist(provider);
			provider.setTreatmentDao(this.treatmentDao);

		} else {
			throw new IProviderDao.ProviderExn("Insertion: Provider with provider id (" + pid + ") already exists. ");
		}
	}

	@Override
	/*
	 * The boolean flag indicates if related treatments should be loaded eagerly.
	 */
	public Provider getProvider(UUID id, boolean includeTreatments) throws ProviderExn {
		/*
		 * TODO retrieve Provider using external key
		 */
		String queryName = "SearchProviderByProviderId";
		TypedQuery<Provider> query = em.createNamedQuery(queryName, Provider.class).setParameter("providerId", id);
		List<Provider> provider = query.getResultList();

		if (provider.size() > 1) {
			throw new ProviderExn("Duplicate provider records: patient id = " + id);
		}else if (provider.size() < 1) {
			throw new ProviderExn("Provider not found: provider id = " + id);
		}else {
			Provider p = provider.get(0);

			em.refresh(p);
			p.setTreatmentDao(this.treatmentDao);
			return p;
		}
	}

	@Override
	/*
	 * By default, we eagerly load related treatments with a provider record.
	 */
	public Provider getProvider(UUID id) throws ProviderExn {
		return getProvider(id, true);
	}

	@Override
	public List<Provider> getProviders() {
		/*
		 * TODO Return a list of all providers (remember to set treatmentDAO)
		 */

		String queryName = "SearchAllProviders";
		TypedQuery<Provider> query = em.createNamedQuery(queryName, Provider.class);

		List<Provider> providers = query.getResultList();

		for (Provider p : providers) {
			p.setTreatmentDao(this.treatmentDao);
		}


		return providers;
	}

	@Override
	public void deleteProviders() {
		Query update = em.createNamedQuery("RemoveAllTreatments");
		update.executeUpdate();
		update = em.createNamedQuery("RemoveAllProviders");
		update.executeUpdate();
	}

}
