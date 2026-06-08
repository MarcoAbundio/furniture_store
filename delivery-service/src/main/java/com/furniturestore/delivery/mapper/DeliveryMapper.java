package com.furniturestore.delivery.mapper;

import com.furniturestore.delivery.dto.response.*;
import com.furniturestore.delivery.model.*;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface DeliveryMapper {

    @Mapping(target = "fullName", expression = "java(c.getFirstName() + \" \" + c.getLastName())")
    CustomerResponse toCustomerResponse(Customer c);

    @Mapping(target = "customerId", source = "customer.id")
    @Mapping(target = "customerName", expression = "java(a.getCustomer().getFirstName() + \" \" + a.getCustomer().getLastName())")
    @Mapping(target = "fullAddress", expression = "java(a.getStreet() + \" #\" + a.getExtNumber() + \", \" + a.getNeighborhood() + \", \" + a.getCity() + \", \" + a.getState() + \" CP \" + a.getZipCode())")
    AddressResponse toAddressResponse(Address a);

    @Mapping(target = "subtotal", expression = "java(item.getUnitPrice().multiply(new java.math.BigDecimal(item.getQuantity())))")
    DeliveryItemResponse toItemResponse(DeliveryItem item);

    @Mapping(target = "customerId", source = "customer.id")
    @Mapping(target = "customerName", expression = "java(d.getCustomer().getFirstName() + \" \" + d.getCustomer().getLastName())")
    @Mapping(target = "customerPhone", source = "customer.phone")
    @Mapping(target = "addressId", source = "address.id")
    @Mapping(target = "deliveryAddress", expression = "java(d.getAddress().getStreet() + \" #\" + d.getAddress().getExtNumber() + \", \" + d.getAddress().getNeighborhood() + \", \" + d.getAddress().getCity())")
    @Mapping(target = "statusId", source = "status.id")
    @Mapping(target = "statusCode", source = "status.code")
    @Mapping(target = "statusDescription", source = "status.description")
    @Mapping(target = "items", source = "items")
    DeliveryResponse toDeliveryResponse(Delivery d);
}
