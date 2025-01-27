<template>
  <div class="w-full h-full">
    <ApexChart
        type="bar"
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

const series = computed(() => [{
  name: '방문자수',
  data: props.data.map(d => d.count)
}])

const chartOptions = {
  chart: {
    type: 'bar',
    toolbar: {
      show: false
    },
    animations: {
      enabled: true,
      dynamicAnimation: {
        speed: 350
      }
    }
  },
  plotOptions: {
    bar: {
      borderRadius: 6,
      columnWidth: '60%',
      dataLabels: {
        position: 'top'
      }
    }
  },
  colors: ['#4F46E5'],
  dataLabels: {
    enabled: true,
    formatter: val => val.toLocaleString() + '명',
    offsetY: -20,
    style: {
      fontSize: '12px',
      fontFamily: 'inherit'
    }
  },
  grid: {
    borderColor: '#f1f1f1',
    strokeDashArray: 4
  },
  xaxis: {
    categories: props.data.map(d => d.hour),
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
      formatter: val => val.toLocaleString(),
      style: {
        fontSize: '12px',
        fontFamily: 'inherit'
      }
    }
  },
  tooltip: {
    y: {
      formatter: val => val.toLocaleString() + '명'
    }
  }
}
</script>