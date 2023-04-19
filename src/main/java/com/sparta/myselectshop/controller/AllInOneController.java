package com.sparta.myselectshop.controller;

import com.sparta.myselectshop.dto.ProductMypriceRequestDto;
import com.sparta.myselectshop.dto.ProductRequestDto;
import com.sparta.myselectshop.dto.ProductResponseDto;
import com.sparta.myselectshop.entity.Product;
import org.springframework.web.bind.annotation.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@RequestMapping("/api")
@RestController
public class AllInOneController {


    // 관심 상품 등록하기
    @PostMapping("/products")
    public ProductResponseDto createProduct(@RequestBody ProductRequestDto requestDto) throws SQLException {
        // Dto를 entity에 저장
        Product product = new Product(requestDto);

        // DB 연결
        Connection connection = DriverManager.getConnection("jdbc:h2:mem:db", "sa", "");

        // DB query 작성
        PreparedStatement ps = connection.prepareStatement("Select max(id) as id from product");
        ResultSet rs = ps.executeQuery();
        if(rs.next()){
            // product id 설정 = product 테이블의 마지막 id + 1
            product.setId(rs.getLong("id")+1);
        }else{
            throw new SQLException("product 테이블의 마지막 id 값을 찾아오지 못했습니다.");
        }


        // 값 초기화
        ps = connection.prepareStatement("insert into product(id, title, image, link, lprice, myprice) values(?, ?, ?, ?, ?, ?)");
        ps.setLong(1, product.getId());
        ps.setString(2, product.getTitle());
        ps.setString(3, product.getImage());
        ps.setString(4, product.getLink());
        ps.setInt(5, product.getLprice());
        ps.setInt(6, product.getMyprice());

        // DB Query 실행
        ps.executeUpdate();

        // db 연결 해제
        ps.close();
        connection.close();

        // 응답 보내기
        return new ProductResponseDto(product);
    }

    // 관심 상품 조회
    @GetMapping("/products")
    public List<ProductResponseDto> getProducts() throws SQLException{
        List<ProductResponseDto> responseDtos = new ArrayList<>();


        //DB 연결
        Connection connection = DriverManager.getConnection("jdbc:h2:mem:db","sa","");

        //DB Query문 작성
        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery("select * from product");

        // db Query로 불러온 data product에 저장해서 List에 나열
        while(rs.next()){
            Product product = new Product();
            product.setId(rs.getLong("id"));
            product.setImage(rs.getString("image"));
            product.setLink(rs.getString("link"));
            product.setLprice(rs.getInt("lprice"));
            product.setMyprice(rs.getInt("myprice"));
            product.setTitle(rs.getString("title"));
            responseDtos.add(new ProductResponseDto(product));
        }


        // DB 연결 해제
        rs.close();
        connection.close();

        // 응답 보내기
        return responseDtos;
    }

    //관심 상품 최저가 등록하기
    @PutMapping("/products/{id}")
    public Long updateProduct(@PathVariable Long id, @RequestBody ProductMypriceRequestDto requestDto) throws SQLException {
        Product product = new Product();

        // DB 연결
        Connection connection = DriverManager.getConnection("jdbc:h2:mem:db","sa","");

        // DB query
        PreparedStatement ps = connection.prepareStatement("select * from product where id = ?");
        ps.setLong(1, id);

        // DB Query
        ResultSet rs = ps.executeQuery();
        if(rs.next()){
            product.setId(rs.getLong("id"));
            product.setImage(rs.getString("image"));
            product.setLink(rs.getString("link"));
            product.setLprice(rs.getInt("lprice"));
            product.setMyprice(rs.getInt("myprice"));
            product.setTitle(rs.getString("title"));
        } else{
            throw new NullPointerException("해당 아이디가 존재하지 않습니다.");
        }

        // DB Query 작성

        ps = connection.prepareStatement("update product set myprice = ? where id = ?");
        ps.setInt(1, requestDto.getMyprice());
        ps.setLong(2, product.getId());

        // DB Query 실행
        ps.executeUpdate();

        // DB 해제
        rs.close();
        ps.close();
        connection.close();


        // 응답보내기
        return product.getId();
    }

}
