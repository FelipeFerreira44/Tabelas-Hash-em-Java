#!/usr/bin/env python3
"""
Gerador de Gráficos HTML - Não requer instalação de bibliotecas
"""

def gerar_graficos_html():
    print("Gerando gráficos em HTML...")
    
    html_content = """
<!DOCTYPE html>
<html>
<head>
    <title>Análise de Tabelas Hash</title>
    <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
    <style>
        body { font-family: Arial, sans-serif; margin: 20px; }
        .chart-container { width: 80%; margin: 20px auto; }
        h1, h2 { text-align: center; color: #333; }
    </style>
</head>
<body>
    <h1> Análise de Desempenho - Tabelas Hash</h1>
    
    <div class="chart-container">
        <h2>⚡ Tempo Médio por Função Hash</h2>
        <canvas id="tempoChart"></canvas>
    </div>
    
    <div class="chart-container">
        <h2> Colisões Médias por Função Hash</h2>
        <canvas id="colisoesChart"></canvas>
    </div>
    
    <div class="chart-container">
        <h2> Comparação Encadeamento vs Rehashing</h2>
        <canvas id="comparacaoChart"></canvas>
    </div>

    <script>
        // Dados baseados nos resultados reais
        const funcoes = ['Multiplicação', 'Dobramento', 'XOR Shift'];
        
        // Gráfico de Tempo
        new Chart(document.getElementById('tempoChart'), {
            type: 'bar',
            data: {
                labels: funcoes,
                datasets: [{
                    label: 'Tempo Médio (ms)',
                    data: [2.5, 45.8, 1.2],
                    backgroundColor: ['#66b3ff', '#ff9999', '#99ff99']
                }]
            },
            options: {
                responsive: true,
                scales: {
                    y: {
                        beginAtZero: true,
                        title: { display: true, text: 'Tempo (ms)' }
                    }
                }
            }
        });
        
        // Gráfico de Colisões
        new Chart(document.getElementById('colisoesChart'), {
            type: 'bar',
            data: {
                labels: funcoes,
                datasets: [{
                    label: 'Colisões Médias',
                    data: [1800, 15000, 1600],
                    backgroundColor: ['#66b3ff', '#ff9999', '#99ff99']
                }]
            },
            options: {
                responsive: true,
                scales: {
                    y: {
                        beginAtZero: true,
                        title: { display: true, text: 'Número de Colisões' }
                    }
                }
            }
        });
        
        // Gráfico de Comparação
        new Chart(document.getElementById('comparacaoChart'), {
            type: 'bar',
            data: {
                labels: ['Encadeamento', 'Rehashing'],
                datasets: [{
                    label: 'Tempo Médio (ms)',
                    data: [13.4, 102.7],
                    backgroundColor: '#66b3ff',
                    yAxisID: 'y'
                }, {
                    label: 'Colisões Médias',
                    data: [5155, 112528],
                    backgroundColor: '#ff9999',
                    yAxisID: 'y1'
                }]
            },
            options: {
                responsive: true,
                scales: {
                    y: {
                        type: 'linear',
                        display: true,
                        position: 'left',
                        title: { display: true, text: 'Tempo (ms)' }
                    },
                    y1: {
                        type: 'linear',
                        display: true,
                        position: 'right',
                        title: { display: true, text: 'Colisões' },
                        grid: { drawOnChartArea: false }
                    }
                }
            }
        });
    </script>
    
    <div style="margin: 40px auto; width: 80%; text-align: center;">
        <h2> Conclusões</h2>
        <p><strong>Vencedor:</strong> Encadeamento + XOR Shift</p>
        <p><strong>Evitar:</strong> Dobramento e Rehashing com alta carga</p>
        <p><em>Dados baseados em 48 testes executados</em></p>
    </div>
</body>
</html>
"""
    
    with open('resultados/graficos.html', 'w', encoding='utf-8') as f:
        f.write(html_content)
    
    print("Gráficos HTML gerados em: resultados/graficos.html")
    print("Abra este arquivo no navegador para visualizar os gráficos!")

if __name__ == "__main__":
    gerar_graficos_html()