<!-- MonthlyChart.vue -->
<template>
  <div class="w-full h-full">
    <ApexChart
        type="line"
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

const series = computed(() => [
  {
    name: '1호점',
    data: props.data.map(d => d.store1)
  },
  {
    name: '2호점',
    data: props.data.map(d => d.store2)
  },
  {
    name: '3호점',
    data: props.data.map(d => d.store3)
  }
])

const chartOptions = {
  chart: {
    type: 'line',
    background: 'transparent',
    toolbar: {
      show: false
    },
    animations: {
      enabled: true,
      easing: 'easeinout',
      speed: 800
    }
  },
  stroke: {
    curve: 'smooth',
    width: 3
  },
  colors: ['#4F46E5', '#10B981', '#F59E0B'],
  grid: {
    borderColor: '#f1f1f1',
    strokeDashArray: 4,
    xaxis: {
      lines: {
        show: true
      }
    }
  },
  markers: {
    size: 4,
    hover: {
      size: 6
    }
  },
  xaxis: {
    categories: props.data.map(d => d.month),
    labels: {
      style: {
        fontSize: '12px',
        fontFamily: 'inherit'
      }
    },
    axisBorder: {
      show: false
    },
    axisTicks: {
      show: false
    }
  },
  yaxis: {
    labels: {
      style: {
        fontSize: '12px',
        fontFamily: 'inherit'
      },
      formatter: value => value.toLocaleString()
    }
  },
  legend: {
    position: 'top',
    horizontalAlign: 'right',
    fontSize: '14px',
    markers: {
      radius: 12
    }
  },
  tooltip: {
    shared: true,
    y: {
      formatter: value => value.toLocaleString()
    }
  }
}
</script>