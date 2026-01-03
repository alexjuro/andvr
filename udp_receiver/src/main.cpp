#include <arpa/inet.h>
#include <sys/socket.h>
#include <unistd.h>

#include <cstdint>
#include <cstring>
#include <iostream>
#include <chrono>

#pragma pack(push, 1)
struct PosePacket {
    uint64_t timestamp_ns;
    float qx;
    float qy;
    float qz;
    float qw;
    float reserved0;
    float reserved1;
};
#pragma pack(pop)

static_assert(sizeof(PosePacket) == 32, "PosePacket must be 32 bytes");

int main() {
    const int PORT = 9944;

    int sock = socket(AF_INET, SOCK_DGRAM, 0);
    if (sock < 0) {
        perror("socket");
        return 1;
    }

    sockaddr_in addr{};
    addr.sin_family = AF_INET;
    addr.sin_port = htons(PORT);
    addr.sin_addr.s_addr = INADDR_ANY;

    if (bind(sock, (sockaddr*)&addr, sizeof(addr)) < 0) {
        perror("bind");
        return 1;
    }

    std::cout << "Listening on UDP port " << PORT << "...\n";

    while (true) {
        PosePacket packet{};
        ssize_t received = recv(sock, &packet, sizeof(packet), 0);

        if (received != sizeof(packet)) {
            std::cerr << "Invalid packet size: " << received << "\n";
            continue;
        }

        uint64_t now_ns =
            std::chrono::duration_cast<std::chrono::nanoseconds>(
                std::chrono::steady_clock::now().time_since_epoch()
            ).count();

        double latency_ms =
            (now_ns - packet.timestamp_ns) / 1e6;

        std::cout
            << "Latency: " << latency_ms << " ms | "
            << "Quat: ["
            << packet.qx << ", "
            << packet.qy << ", "
            << packet.qz << ", "
            << packet.qw << "]\n";
    }

    close(sock);
    return 0;
}
