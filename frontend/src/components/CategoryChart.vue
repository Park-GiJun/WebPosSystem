<template>
  <div class="w-full h-full">
    <ApexChart
        type="donut"
        :options="chartOptions"
        :series="series"
        height="100%"
    />
  </div>
</template>

<script setup>
import { computed } from 'vue'
import ApexChart from 'vue3-apexcharts'

const props = defineProps({
  data: {
    type: Array,
    required: true,
    default: () => []
  }
})

const series = computed(() => props.data.map(d => d.value))

const chartOptions = {
  chart: {
    type: 'donut',
    animations: {
      speed: 500,
      animateGradually: {
        enabled: true,
        delay: 150
      }
    }
  },
  labels: props.data.map(d => d.name),
  colors: ['#4F46E5', '#10B981', '#F59E0B', '#EF4444'],
  legend: {
    position: 'bottom',
    fontSize: '14px',
    markers: {
      width: 12,
      height: 12,
      radius: 12
    }
  },
  stroke: {
    show: false
  },
  dataLabels: {
    enabled: true,
    formatter: val => val.toFixed(1) + '%',
    style: {
      fontSize: '12px',
      fontFamily: 'inherit',
      fontWeight: 'medium'
    }
  },
  plotOptions: {
    pie: {
      donut: {
        size: '70%',
        labels: {
          show: true,
          total: {
            show: true,
            label: '총 매출',
            formatter: function(w) {
              return w.globals.seriesTotals.reduce((a, b) => a + b, 0) + '%'
            }
          }
        }
      }
    }
  },
  tooltip: {
    y: {
      formatter: val => val + '%'
    }
  }
}
</script>