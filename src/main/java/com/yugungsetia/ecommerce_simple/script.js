import http from 'k6/http';
import { check, sleep } from 'k6';

// Konfigurasi skenario test
export const options = {
    stages: [
        { duration: '10s', target: 50 },   // Ramp-up ke 50 VU dalam 10 detik
        { duration: '50s', target: 100 },  // Ramp-up ke 100 VU dalam 50 detik
        { duration: '10s', target: 0 },    // Ramp-down ke 0 VU dalam 10 detik
    ],
    thresholds: {
        'http_req_duration': ['p(95)<200'], // 95% request harus <= 200ms
    },
};

// URL endpoint yang akan di test
const BASE_URL = 'http://localhost:8081/api/v1/products';

export default function () {
    // Melakukan GET request ke API
    const response = http.get(BASE_URL);

    // Validasi response
    check(response, {
        'status is 200': (r) => r.status === 200,
        'response time < 200ms': (r) => r.timings.duration < 200,
    });

    // Simulasi thinking time antar request
    sleep(1);
}