package code.vanilson.marketplace.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.persistence.*;
import lombok.Getter;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Entity
@Table(name = "tb_products")
@JsonPropertyOrder({"id", "name", "quantity", "version"})
public class Product implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id", nullable = false)
    @JsonProperty("id")
    private Integer productId;
    private String name;
    private Integer quantity;
    private Integer version;


    public Product() {
        // default constructor
    }

    public Product(String name, int quantity) {
        this.name = name;
        this.quantity = quantity;
    }

    public Product(Integer id, String name, Integer quantity, Integer version) {
        this.productId = id;
        this.name = name;
        this.quantity = quantity;
        this.version = version;
    }

    public Product(Integer id, String name, Integer quantity) {
        this.productId = id;
        this.name = name;
        this.quantity = quantity;
    }

    public void setProductId(Integer id) {
        this.productId = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Product product = (Product) o;
        return Objects.equals(productId, product.productId) && Objects.equals(name, product.name) && Objects.equals(quantity, product.quantity) && Objects.equals(version, product.version);
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(productId);
        result = 31 * result + Objects.hashCode(name);
        result = 31 * result + Objects.hashCode(quantity);
        result = 31 * result + Objects.hashCode(version);
        return result;
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + productId +
                ", name='" + name + '\'' +
                ", quantity=" + quantity +
                ", version=" + version +
                '}';
    }


}
