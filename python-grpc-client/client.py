import grpc

import product_pb2
import product_pb2_grpc

def main():
    with grpc.insecure_channel("localhost:9091") as channel:
        stub = product_pb2_grpc.ProductServiceStub(channel)

        request = product_pb2.ProductRequest(id=1)

        response = stub.getProductDetails(request, timeout=5)

        print("Product response:")
        print(f"ID: {response.id}")
        print(f"Name: {response.name}")
        print(f"Description: {response.description}")
        print(f"Price: {response.price}")
        print(f"Quantity: {response.quantity}")

if __name__ == "__main__":
    main()