package com.rms.rest.handler;
import com.rms.domain.core.Product;
import com.rms.domain.sales.OrderItem;
import com.rms.repository.OrderItemRepository;
import com.rms.rest.dto.OrderItemDto;
import com.rms.rest.dto.common.PaginatedResultDto;
import com.rms.rest.exception.ErrorCodes;
import com.rms.rest.exception.ResourceNotFoundException;
import com.rms.rest.exception.ResourceRelatedException;
import com.rms.rest.exception.Response;
import com.rms.rest.modelmapper.OrderItemMapper;
import com.rms.rest.modelmapper.common.PaginationMapper;
import com.rms.service.OrderItemService;
import com.rms.service.ProductService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
@AllArgsConstructor
public class OrderItemHandler {
    private ProductService productService;
    private OrderItemService orderItemService;
    private OrderItemMapper mapper;
    private PaginationMapper paginationMapper ;

    public ResponseEntity<?> getAll(Integer page , Integer size)
    {
        Page<OrderItem> OrderItems = orderItemService.getAll(page, size);
        List<OrderItemDto> dtos =mapper.toDto(OrderItems.getContent());
        PaginatedResultDto<OrderItemDto> paginatedResultDto = new PaginatedResultDto<>();
        paginatedResultDto.setData(dtos);
        paginatedResultDto.setPagination(paginationMapper.toDto(OrderItems));
        return ResponseEntity.ok(paginatedResultDto);
    }

    public List<OrderItemDto> findOrderItemsByOrderId(Integer orderId)
    {
        List<OrderItem> OrderItems = orderItemService.getOrderItemsByOrderId(orderId);
        List<OrderItemDto> dtos =mapper.toDto(OrderItems);
        return dtos;
    }


    public  ResponseEntity<?>  save(OrderItemDto orderItemDto) {
        Product product =productService.getById(orderItemDto.getProduct().getId())
                .orElseThrow(() -> new ResourceNotFoundException(Product.class.getSimpleName(), orderItemDto.getProduct().getId()));
       OrderItem orderItem =mapper.toEntity(orderItemDto);
       orderItem.setUnitPrice(product.getCashPrice()*orderItem.getQuantity());
        OrderItemDto dto = mapper.toDto(orderItemService.save(orderItem));
        return ResponseEntity.ok(dto);
    }


    public ResponseEntity<?> getById(Integer id) {
        OrderItem orderItem= orderItemService.getById(id)
                .orElseThrow(() -> new ResourceNotFoundException(OrderItem.class.getSimpleName(),id));
        OrderItemDto dto = mapper.toDto(orderItem);
        return ResponseEntity.ok(dto);
    }

    public ResponseEntity<?> update(OrderItemDto orderItemDto,Integer id) {
        OrderItem orderItem = orderItemService.getById(id)
                .orElseThrow(() -> new ResourceNotFoundException(OrderItem.class.getSimpleName(),id));
        mapper.updateEntityFromDto(orderItemDto, orderItem);
        orderItemService.update(orderItem);
        OrderItemDto dto = mapper.toDto(orderItem);
        return ResponseEntity.ok(dto);
    }
    public ResponseEntity<?> delete(Integer id) {
        OrderItem orderItem = orderItemService.getById(id)
                .orElseThrow(() -> new ResourceNotFoundException(OrderItem.class.getSimpleName(),id));
        try {
            orderItemService.delete(orderItem);
        } catch (Exception exception) {
            throw new ResourceRelatedException(OrderItem.class.getSimpleName(), "Id", id.toString(), ErrorCodes.RELATED_RESOURCE.getCode());
        }
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(new Response("deleted"));


    }


}
