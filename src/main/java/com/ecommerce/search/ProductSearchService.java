package com.ecommerce.search;

import com.ecommerce.product.Product;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;

import org.hibernate.search.mapper.orm.Search;

import java.util.List;

@ApplicationScoped
public class ProductSearchService {

    @Inject
    EntityManager entityManager;

    public List<Product> searchProducts(String query) {
        return Search.session(entityManager)
                .search(Product.class)
                .where(f -> f.match()
                        .fields("name", "description")
                        .matching(query))
                .fetchHits(20);
    }
}
