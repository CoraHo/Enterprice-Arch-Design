package edu.stevens.cs548.clinic.data;

import jakarta.persistence.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

/**
 * Entity implementation class for Entity: Patient
 *
 */
@NamedQueries({
	@NamedQuery(
		name="SearchProviderByProviderId",
		query="select p from Provider p where p.providerId = :providerId"),
	@NamedQuery(
		name="CountProviderByProviderId",
		query="select count(p) from Provider p where p.providerId = :providerId"),
	@NamedQuery(
		name = "RemoveAllProviders", 
		query = "delete from Provider p")
})
// TODO
@Entity
@Table(indexes = @Index(columnList = "providerId"))
public class Provider implements Serializable {
		
	private static final long serialVersionUID = -876909316791083094L;

	// TODO JPA annotations
	@Id
	@GeneratedValue
	private long id;
	
	// TODO
	@Column(nullable = false, unique = true)
	private UUID providerId;
	
	private String npi;

	private String name;
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
	
	public UUID getProviderId() {
		return providerId;
	}

	public void setProviderId(UUID providerId) {
		this.providerId = providerId;
	}

	public String getNpi() {
		return npi;
	}

	public void setNpi(String npi) {
		this.npi = npi;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	// TODO JPA annotations (propagate persist of provider to treatments)
	@OneToMany(cascade = CascadeType.PERSIST, mappedBy = "provider")
	private Collection<Treatment> treatments;


	public boolean administers(Treatment t) {
		return treatments.contains(t);
	}

	/**
	 * Call both this and Patient::addTreatment
	 */
	public void addTreatment (Treatment t) {
		/*
		 * TODO complete this operation (see patient entity)
		 */
		treatments.add(t);
		if (t.getProvider() != this) {
			t.setProvider(this);
		}
	}

	public Provider() {
		super();
		treatments = new ArrayList<Treatment>();
	}

}
